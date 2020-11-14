package sonar.reactorbuilder.common.reactors;

import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;
import sonar.reactorbuilder.common.reactors.templates.OverhaulFissionTemplate;
import sonar.reactorbuilder.common.reactors.templates.OverhaulTurbine;
import sonar.reactorbuilder.common.reactors.templates.UnderhaulSFRTemplate;

public enum TemplateType {
    UNDERHAUL_SFR(false,"Underhaul SFR", UnderhaulSFRTemplate::new),
    OVERHAUL_SFR(true,"Overhaul SFR", OverhaulFissionTemplate.SFR::new),
    OVERHAUL_MSR(true,"Overhaul MSR", OverhaulFissionTemplate.MSR::new),
    OVERHAUL_TURBINE(true,"Overhaul Turbine", OverhaulTurbine::new);

    public boolean overhaul;
    public String fileType;
    public IReactorProvider creator;

    TemplateType(boolean overhaul, String s, IReactorProvider creator) {
        this.overhaul = overhaul;
        this.fileType = s;
        this.creator = creator;
    }

    public interface IReactorProvider{

        AbstractTemplate create();

    }
}
