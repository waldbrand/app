package de.topobyte.apps.viewer.label;

import com.slimjars.dist.gnu.trove.map.TIntIntMap;
import com.slimjars.dist.gnu.trove.map.hash.TIntIntHashMap;

import java.util.List;

import de.topobyte.mapocado.mapformat.Mapfile;
import de.waldbrandapp.Waldbrand;

public class WaldbrandMapping
{

  private final TIntIntMap waldbrandClassIdToConstant = new TIntIntHashMap();

  private RenderClassMapping classMappingWaldbrand;

  void processRenderConfig(Mapfile mapfileWaldbrand,
                           RenderConfig renderConfig)
  {
    classMappingWaldbrand = new RenderClassMapping(mapfileWaldbrand, renderConfig);

    waldbrandClassIdToConstant.clear();
    for (String type : Waldbrand.getLabelTypes()) {
      int constant = Waldbrand.getConstant(type);
      List<RenderClass> renderClasses = renderConfig.getRenderClasses(type);
      for (RenderClass renderClass : renderClasses) {
        waldbrandClassIdToConstant.put(renderClass.classId, constant);
      }
    }
  }

  public int getConstantForClassId(int type)
  {
    return waldbrandClassIdToConstant.get(type);
  }

  public List<RenderClass> getRenderClasses(int ref)
  {
    return classMappingWaldbrand.getRenderClasses(ref);
  }

}
