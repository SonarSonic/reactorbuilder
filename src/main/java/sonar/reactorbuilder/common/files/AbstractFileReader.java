package sonar.reactorbuilder.common.files;

import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;

import java.io.File;

public abstract class AbstractFileReader {

    public abstract AbstractTemplate readTemplate(File file);

}
