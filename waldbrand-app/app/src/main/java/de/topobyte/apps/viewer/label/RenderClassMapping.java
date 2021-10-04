// Copyright 2021 Sebastian Kuerten
//
// This file is part of waldbrand-app.
//
// waldbrand-app is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// waldbrand-app is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with waldbrand-app. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.apps.viewer.label;

import com.slimjars.dist.gnu.trove.map.TIntObjectMap;
import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import java.util.List;

import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.io.Metadata;

/**
 * This class maps object ids from the mapfile (refs) to label rendering classes.
 */
public class RenderClassMapping
{

  TIntObjectMap<List<RenderClass>> refToRenderClasses = new TIntObjectHashMap<>();

  public RenderClassMapping(Mapfile mapfile,
                            RenderConfig renderConfig)
  {
    Metadata metadata = mapfile.getMetadata();
    for (int ref = 0; ref < metadata.getPoolForRefs().getNumberOfEntries(); ref++) {
      String type = metadata.getPoolForRefs().getString(ref);
      List<RenderClass> renderClasses = renderConfig.getRenderClasses(type);
      refToRenderClasses.put(ref, renderClasses);
    }
  }

  public List<RenderClass> getRenderClasses(int ref)
  {
    return refToRenderClasses.get(ref);
  }

}
