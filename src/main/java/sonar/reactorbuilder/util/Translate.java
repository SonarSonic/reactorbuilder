package sonar.reactorbuilder.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.reactorbuilder.ReactorBuilder;

import javax.annotation.Nonnull;

public enum Translate {

    //gui buttons
    PASTE_FILE(true, "paste_file"),
    BUILD_REACTOR(true, "build_reactor"),
    DESTROY_REACTOR(true, "destroy_reactor"),
    PREV_PAGE(true, "prev_page"),
    NEXT_PAGE(true, "next_page"),

    //builder status
    STATUS(true, "status"),
    STATUS_IDLE(true, "status.idle"),
    STATUS_FINISHED(true, "status.finished"),
    STATUS_BUILDING(true, "status.building"),
    STATUS_DESTROYING(true, "status.destroying"),

    //stats
    TEMPLATE_FILE_NAME(true, "template.file_name"),
    TEMPLATE_REACTOR_TYPE(true, "template.reactor_type"),
    TEMPLATE_DIMENSIONS(true, "template.dimensions"),
    TEMPLATE_FUEL_TYPE(true, "template.fuel_type"),
    TEMPLATE_FUEL_TYPES(true, "template.fuel_types"),
    TEMPLATE_IRRADIATOR_FILTERS(true, "template.irradiator_filters"),
    TEMPLATE_COMPONENTS(true, "template.components"),
    TEMPLATE_CASING(true, "template.casing"),
    TEMPLATE_EDGES(true, "template.edges"),

    //general
    CASING_CONFIG(true, "general.casing_config"),
    REQUIRED_COMPONENTS(true, "general.required_components"),
    FEATURE_DISABLED(true, "general.disabled_feature"),

    //builder passes
    PASS_REMOVING_COMPONENTS(true, "pass.removing_components"),
    PASS_PLACING_COMPONENTS(true, "pass.placing_components"),
    PASS_PLACING_CASINGS(true, "pass.placing_casings"),
    PASS_PLACING_EDGES(true, "pass.placing_edges"),
    PASS_PLACING_COILS(true, "pass.placing_coils"),
    PASS_PLACING_SHAFTS(true, "pass.placing_shafts"),
    PASS_PLACING_BLADES(true, "pass.placing_blades"),
    PASS_SETTING_FILTERS(true, "pass.setting_filters"),

    //file status
    FILE_NO_TEMPLATE(true, "file.no_template"),
    FILE_ERROR(true, "file.error"),
    FILE_NO_FILE(true, "file.no_file"),
    FILE_MISSING_EXTENSION(true, "file.missing_extension"),
    FILE_INVALID_EXTENSION(true, "file.invalid_extension"),

    //energy
    ENERGY_INFINITE(true, "energy.infinite"),

    ;

    String key;

    Translate(boolean prefix, String key) {
        this.key = prefix ? "info." + ReactorBuilder.MODID + "." + key : key;
    }

    @Nonnull
    public String t() {
        return Util.translate(key);
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    public String format(Object... args) {
        return I18n.format(key, args);
    }

    @Nonnull
    public TextComponentTranslation getTextComponent() {
        return new TextComponentTranslation(key);
    }

    @Nonnull
    public TextComponentTranslation getTextComponent(Object... args) {
        return new TextComponentTranslation(key, args);
    }

    @Nonnull
    @Override
    public String toString() {
        return t();
    }

}
