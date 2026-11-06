package ies.sequeros.psp.airfigther.view;

import ies.sequeros.psp.airfigther.model.enemy.EnemyType;

public class EnemySpriterMapper {
    public static ImageResource getImageResource(EnemyType type) {
        ImageResource imageResource = null;
         switch (type) {
            case B17 -> { imageResource=ImageResource.B17; break; }
             case F16 ->  { imageResource=ImageResource.F16; break; }
             case BOMBARDERO ->  { imageResource=ImageResource.BOMBARDERO; break; }
             case XWIN -> {imageResource=ImageResource.XWIN;break;}
             default -> imageResource=ImageResource.B17;
            }
        return imageResource;
    }
}
