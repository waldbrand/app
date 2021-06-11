// Copyright 2021 Sebastian Kuerten
//
// This file is part of stadtplan-app.
//
// stadtplan-app is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// stadtplan-app is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with stadtplan-app. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.apps.viewer.search.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.slimjars.dist.gnu.trove.set.TIntSet;
import com.slimjars.dist.gnu.trove.set.hash.TIntHashSet;

import java.util.List;

import de.topobyte.apps.maps.atestcity.R;
import de.topobyte.apps.viewer.freemium.FreemiumUtil;
import de.topobyte.apps.viewer.poi.Categories;
import de.topobyte.apps.viewer.poi.Group;
import de.topobyte.apps.viewer.poi.ListDrawables;
import de.topobyte.apps.viewer.poi.PoiTypeInfo;
import de.topobyte.apps.viewer.poi.category.Category;
import de.topobyte.apps.viewer.poi.category.DatabaseCategory;
import de.topobyte.apps.viewer.search.Common;
import de.topobyte.apps.viewer.search.HelpSearchDialog;
import de.topobyte.apps.viewer.search.PoiTypeSelection;
import de.topobyte.apps.viewer.search.ResultOrder;
import de.topobyte.apps.viewer.search.ResultState;
import de.topobyte.apps.viewer.search.ResultsBuffer;
import de.topobyte.apps.viewer.search.SearchQuery;
import de.topobyte.apps.viewer.search.SearchResultsReceiver;
import de.topobyte.apps.viewer.search.TypeSelection;
import de.topobyte.apps.viewer.search.categories.CategoriesDialog;
import de.topobyte.apps.viewer.search.widget.EnumMenu;
import de.topobyte.apps.viewer.widget.Clearable;
import de.topobyte.apps.viewer.widget.TextWatcherAdapter;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.nomioc.luqe.dao.MatchMode;
import de.topobyte.nomioc.luqe.model.SqEntity;

public class SearchFragment extends BaseGeocodingFragment implements
    SearchResultsReceiver
{

  public static final String EXTRA_X = "map-x";
  public static final String EXTRA_Y = "map-y";

  private static final String LOG_TAG = "search";

  private static final String STATE_RESULT_STATE = "result-state";

  private boolean unlocked = false;
  private SearchQuery lastQueuedQuery = null;

  private ResultState resultState = ResultState.NOT_INITIALIZED;

  private MatchMode matchMode = MatchMode.ANYWHERE;
  private ResultOrder resultOrder = ResultOrder.ALPHABETICALLY;
  private TypeSelection typeSelection;

  private EditText searchInput;

  private ImageButton buttonMatchMode;
  private ImageButton buttonResultOrder;
  private ImageButton buttonCategories;
  private ImageButton buttonHelp;

  private WorkerFragment workerFragment;

  private Point mapCenter;

  private TextWatcherAdapter textWatcherAdapter;

  public void setWorkerFragment(WorkerFragment workerFragment)
  {
    this.workerFragment = workerFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Log.i(LOG_TAG, "SearchFragment.onCreate() "
        + (savedInstanceState != null));

    Bundle extras = getArguments();
    int mx = extras.getInt(EXTRA_X);
    int my = extras.getInt(EXTRA_Y);
    mapCenter = new Point(mx, my);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle state)
  {
    super.onCreateView(inflater, container, state);
    Log.i(LOG_TAG, "SearchFragment.onCreateView()");

    View view = inflater.inflate(R.layout.search, container, false);

    searchInput = view.findViewById(R.id.searchInput);
    Clearable.setClearable(searchInput);

    buttonMatchMode = view.findViewById(R.id.buttonMatchMode);
    buttonResultOrder = view.findViewById(R.id.buttonOrder);
    buttonCategories = view.findViewById(R.id.buttonFilter);
    buttonHelp = view.findViewById(R.id.buttonHelp);

    if (state != null) {
      Log.i(LOG_TAG, "fragment state != null");
      resultState = ResultState.values()[state.getInt(STATE_RESULT_STATE)];

      FragmentManager fm = getChildFragmentManager();
      if (resultState == ResultState.NONE) {
        noResultsFragment = (NoResultsFragment) fm
            .findFragmentById(R.id.results);
      } else if (resultState == ResultState.SOME) {
        resultsFragment = (ResultsFragment) fm
            .findFragmentById(R.id.results);
        resultsFragment.setDatabase(this);
      }
    }

    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);

    Log.i(LOG_TAG, "resultState: " + resultState);
    ResultsBuffer resultsBuffer = workerFragment.getResultsBuffer();
    resultsBuffer.setResultReceiver(this);
    Log.i(LOG_TAG, "bufferState: " + resultsBuffer.getState());

    if (resultsBuffer.getState() == ResultState.NONE) {
      showNoResults();
    } else if (resultsBuffer.getState() == ResultState.SOME) {
      showResults(resultsBuffer.getQuery(), resultsBuffer.getResults());
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
    Log.i(LOG_TAG, "SearchFragment.onSaveInstanceState()");
    outState.putInt(STATE_RESULT_STATE, resultState.ordinal());
  }

  @Override
  public void onResume()
  {
    super.onResume();
    Log.i(LOG_TAG, "SearchFragment.onResume()");

    updateFreemiumStuff();

    openDatabase();

    SearchQuery query = loadQuery();

    // if (query.getQuery().length() != 0) {
    getActivity().getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    // }

    searchInput.setText(query.getQuery());
    searchInput.setSelection(searchInput.getText().length());
    matchMode = query.getMatchMode();
    resultOrder = query.getResultOrder();
    typeSelection = query.getTypeSelection();
    syncIcons();

    try {
      ListDrawables.initialize(ldb);
    } catch (QueryException e) {
      Log.e(LOG_TAG, "Error while initializing categories", e);
    }

    Log.i(LOG_TAG, "resultState: " + resultState);
    if (resultState == ResultState.NOT_INITIALIZED) {
      update(query);
    }

    SearchQuery lastQuery = workerFragment.getSearchWorker()
        .getLastIssuedQuery();
    Log.i(LOG_TAG, "lastQueuedQuery: " + lastQuery);
    if (lastQuery == null) {
      update(query);
    }

    setupListeners();
  }

  private void updateFreemiumStuff()
  {
    unlocked = FreemiumUtil.showPremiumFeatures(getActivity());

    int visibilityLockableViews = unlocked ? View.VISIBLE : View.GONE;
    int visibilityUnlockButton = unlocked ? View.GONE : View.VISIBLE;

    buttonMatchMode.setVisibility(visibilityLockableViews);
    buttonResultOrder.setVisibility(visibilityLockableViews);
    buttonCategories.setVisibility(visibilityLockableViews);
    buttonHelp.setVisibility(visibilityLockableViews);
  }

  private void syncIcons()
  {
    syncMatchModeButton();
    syncResultOrderButton();
  }

  private void syncMatchModeButton()
  {
    int resId;
    switch (matchMode) {
      default:
      case ANYWHERE:
        resId = R.drawable.match_mode_anywhere;
        break;
      case BEGIN_WITH:
        resId = R.drawable.match_mode_start_with;
        break;
      case END_WITH:
        resId = R.drawable.match_mode_end_with;
        break;
      case EXACT:
        resId = R.drawable.match_mode_exact;
        break;
    }
    buttonMatchMode.setImageResource(resId);
  }

  private void syncResultOrderButton()
  {
    int resId;
    switch (resultOrder) {
      default:
      case ALPHABETICALLY:
        resId = R.drawable.sort_a_z;
        break;
      case ALPHABETICALLY_INVERSE:
        resId = R.drawable.sort_z_a;
        break;
      case BY_DISTANCE:
        resId = R.drawable.sort_distance;
        break;
    }
    buttonResultOrder.setImageResource(resId);
  }

  @Override
  public void onPause()
  {
    super.onPause();
    Log.i(LOG_TAG, "SearchFragment.onPause()");

    Log.i(LOG_TAG, "removing TextChangedListener");
    searchInput.removeTextChangedListener(textWatcherAdapter);

    Log.i(LOG_TAG, "closing database");
    closeDatabase();
  }

  private void setupListeners()
  {
    textWatcherAdapter = new TextWatcherAdapter(searchInput,
        (view, text) -> inputChanged());

    searchInput.addTextChangedListener(textWatcherAdapter);

    buttonMatchMode.setOnClickListener(v -> {
      int[] ids = new int[]{
          R.id.anywhere, R.id.exact, R.id.start_with, R.id.end_with};

      MatchMode[] values = new MatchMode[]{
          MatchMode.ANYWHERE, MatchMode.EXACT,
          MatchMode.BEGIN_WITH, MatchMode.END_WITH};

      EnumMenu<MatchMode> enumMenu = new EnumMenu<MatchMode>(
          getActivity(), v, R.menu.popup_menu_match_mode,
          matchMode, ids, values)
      {

        @Override
        protected void clicked(MatchMode value)
        {
          setMatchMode(value);
        }

      };
      enumMenu.show();
    });

    buttonResultOrder.setOnClickListener(v -> {
      int[] ids = new int[]{R.id.alphabetic,
          R.id.alphabetic_inverse, R.id.distance};
      ResultOrder[] values = new ResultOrder[]{
          ResultOrder.ALPHABETICALLY,
          ResultOrder.ALPHABETICALLY_INVERSE,
          ResultOrder.BY_DISTANCE};

      EnumMenu<ResultOrder> enumMenu = new EnumMenu<ResultOrder>(
          getActivity(), v, R.menu.popup_menu_result_order,
          resultOrder, ids, values)
      {

        @Override
        protected void clicked(ResultOrder value)
        {
          setResultOrder(value);
        }

      };
      enumMenu.show();
    });

    buttonCategories.setOnClickListener(v -> {
      PopupMenu menu = new PopupMenu(getActivity(), v);
      menu.inflate(R.menu.popup_menu_categories);
      menu.show();

      menu.setOnMenuItemClickListener(item -> {
        switch (item.getItemId()) {
          case R.id.select_all:
            pickAllCategories();
            break;
          case R.id.select_streets:
            pickStreets();
            break;
          case R.id.select_food:
            pickFood();
            break;
          case R.id.pick:
            showCategoriesDialog();
            break;
        }
        return false;
      });
    });

    buttonHelp.setOnClickListener(view -> {
      HelpSearchDialog helpDialog = new HelpSearchDialog();
      helpDialog.show(getActivity().getSupportFragmentManager(), null);
    });
  }

  protected void pickAllCategories()
  {
    Categories categories = Categories.getSearchInstance();
    categories.pickAll(getActivity());
    reloadSelectedCategories();
  }

  protected void pickStreets()
  {
    Categories categories = Categories.getSearchInstance();
    categories.pickStreets(getActivity());
    reloadSelectedCategories();
  }

  protected void pickFood()
  {
    Categories categories = Categories.getSearchInstance();
    categories.pickFood(getActivity());
    reloadSelectedCategories();
  }

  protected void showCategoriesDialog()
  {
    CategoriesDialog categoriesDialog = new CategoriesDialog();
    categoriesDialog.show(getFragmentManager(), null);
  }

  protected void setMatchMode(MatchMode matchMode)
  {
    if (this.matchMode == matchMode) {
      return;
    }
    this.matchMode = matchMode;
    syncMatchModeButton();
    inputChanged();
  }

  protected void setResultOrder(ResultOrder resultOrder)
  {
    if (this.resultOrder == resultOrder) {
      return;
    }
    this.resultOrder = resultOrder;
    syncResultOrderButton();
    inputChanged();
  }

  protected void inputChanged()
  {
    Log.i(LOG_TAG, "inputChanged()");
    String queryString = searchInput.getText().toString();

    SearchQuery query =
        new SearchQuery(queryString, matchMode, resultOrder, mapCenter, typeSelection);

    update(query);
  }

  private void update(SearchQuery query)
  {
    Log.i(LOG_TAG, "Update: " + query);
    if (lastQueuedQuery != null && lastQueuedQuery.equals(query)) {
      return;
    }
    Log.i(LOG_TAG, "Queue query");
    storeQuery(query);
    lastQueuedQuery = query;
    workerFragment.getSearchWorker().queueQuery(query);
  }

  private NoResultsFragment noResultsFragment;
  private ResultsFragment resultsFragment;

  private void setupFragmentState(ResultState state)
  {
    Log.i(LOG_TAG, "current fragment state: " + resultState);
    Log.i(LOG_TAG, "target fragment state: " + state);
    if (state == resultState) {
      return;
    }

    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

    if (state == ResultState.SOME) {
      resultsFragment = new ResultsFragment();
      resultsFragment.setDatabase(this);
      transaction.replace(R.id.results, resultsFragment);
    } else if (state == ResultState.NONE) {
      noResultsFragment = new NoResultsFragment();
      transaction.replace(R.id.results, noResultsFragment);
    }

    transaction.commit();
  }

  private void showNoResults()
  {
    setupFragmentState(ResultState.NONE);
    resultState = ResultState.NONE;
  }

  private void showResults(SearchQuery query, List<SqEntity> results)
  {
    setupFragmentState(ResultState.SOME);
    resultState = ResultState.SOME;

    resultsFragment.showResults(query, results);
  }

  private void storeQuery(SearchQuery query)
  {
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(getActivity());

    Editor editor = prefs.edit();
    editor.putString(Common.PREF_SEARCH_QUERY, query.getQuery());
    editor.putString(Common.PREF_SEARCH_QUERY_EXACT, matchMode.name());
    editor.putString(Common.PREF_SEARCH_QUERY_ORDER, resultOrder.name());
    editor.commit();
  }

  private SearchQuery loadQuery()
  {
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(getActivity());
    String query = prefs.getString(Common.PREF_SEARCH_QUERY, "");
    MatchMode matchMode = unlocked ? get(prefs,
        Common.PREF_SEARCH_QUERY_EXACT, MatchMode.class,
        MatchMode.ANYWHERE) : MatchMode.ANYWHERE;
    ResultOrder resultOrder = unlocked ? get(prefs,
        Common.PREF_SEARCH_QUERY_ORDER, ResultOrder.class,
        ResultOrder.ALPHABETICALLY) : ResultOrder.ALPHABETICALLY;
    TypeSelection typeSelection = loadTypeSelection(prefs);

    return new SearchQuery(query, matchMode, resultOrder, mapCenter,
        typeSelection);
  }

  private TypeSelection loadTypeSelection(SharedPreferences prefs)
  {
    PoiTypeInfo typesInfo = PoiTypeInfo.getInstance(ldb);

    Categories categories = Categories.getSearchInstance();

    boolean includeStreets = unlocked ? categories.isEnabled(prefs,
        Categories.PREF_KEY_STREETS) : true;

    PoiTypeSelection poiTypes = PoiTypeSelection.ALL;
    TIntSet typeIds = new TIntHashSet();

    if (!unlocked) {
      for (Group group : categories.getGroups()) {
        for (Category category : group.getChildren()) {
          if (category instanceof DatabaseCategory) {
            DatabaseCategory dcat = (DatabaseCategory) category;
            for (String typeId : dcat.getIdentifiers()) {
              int id = typesInfo.getTypeId(typeId);
              typeIds.add(id);
            }
          }
        }
      }

      return new TypeSelection(includeStreets, poiTypes, typeIds);
    }

    boolean includeOthers = categories.isEnabled(prefs,
        Categories.PREF_KEY_OTHERS);

    for (Group group : categories.getGroups()) {
      for (Category category : group.getChildren()) {
        if (category instanceof DatabaseCategory) {
          DatabaseCategory dcat = (DatabaseCategory) category;
          boolean enabled = categories.isEnabled(prefs, dcat);
          if (!enabled) {
            poiTypes = PoiTypeSelection.SOME;
            continue;
          }
          for (String typeId : dcat.getIdentifiers()) {
            int id = typesInfo.getTypeId(typeId);
            typeIds.add(id);
          }
        }
      }
    }

    if (includeOthers) {
      if (poiTypes != PoiTypeSelection.ALL) {
        TIntSet othersIds = typesInfo.determineOthers(categories);
        typeIds.addAll(othersIds);
      }
    }

    if (typeIds.isEmpty()) {
      poiTypes = PoiTypeSelection.NONE;
    }

    return new TypeSelection(includeStreets, poiTypes, typeIds);
  }

  private static <T extends Enum<T>> T get(SharedPreferences prefs,
                                           String key, Class<T> type, T defaultValue)
  {
    String value = prefs.getString(key, null);
    if (value == null) {
      return defaultValue;
    }
    for (T t : type.getEnumConstants()) {
      if (t.name().equals(value)) {
        return t;
      }
    }
    return null;
  }

  @Override
  public void reportNone(final SearchQuery query)
  {
    showNoResults();
  }

  @Override
  public void report(final SearchQuery query, final List<SqEntity> results)
  {
    showResults(query, results);
  }

  public void reloadSelectedCategories()
  {
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(getActivity());
    typeSelection = loadTypeSelection(prefs);
    inputChanged();
  }

}
