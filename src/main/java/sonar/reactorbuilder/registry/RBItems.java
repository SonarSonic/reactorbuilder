package sonar.reactorbuilder.registry;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RBItems {

    //@GameRegistry.ObjectHolder("reactorbuilder:designtemplate")
    //public static DesignTemplateItem designTemplate;


    @SideOnly(Side.CLIENT)
    public static void initModels() {
        //designTemplate.initModel();
    }
}
