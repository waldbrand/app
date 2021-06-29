package de.topobyte.apps.viewer.label;

import com.slimjars.dist.gnu.trove.map.TIntObjectMap;
import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.io.Metadata;

/**
 * This class maps object ids from the mapfile (refs) to label rendering classes.
 */
public class RenderClassMapping
{

  TIntObjectMap<RenderClass> refToRenderClass = new TIntObjectHashMap<>();

  public RenderClassMapping(Mapfile mapfile,
                            RenderConfig renderConfig)
  {
    Metadata metadata = mapfile.getMetadata();
    for (int ref = 0; ref < metadata.getPoolForRefs().getNumberOfEntries(); ref++) {
      String type = metadata.getPoolForRefs().getString(ref);
      RenderClass renderClass = renderConfig.getRenderClass(type);
      refToRenderClass.put(ref, renderClass);
    }
  }

  public RenderClass getRenderClass(int ref)
  {
    return refToRenderClass.get(ref);
  }

}
