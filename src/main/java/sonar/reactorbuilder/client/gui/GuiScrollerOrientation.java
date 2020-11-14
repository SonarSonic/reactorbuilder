package sonar.reactorbuilder.client.gui;

public enum GuiScrollerOrientation {

    VERTICAL, HORIZONTAL;

    public boolean isVertical() {
        return this == VERTICAL;
    }

    public boolean isHorizontal() {
        return this == HORIZONTAL;
    }
}