package sonar.reactorbuilder.network.templates;

import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class TemplateManager {

    protected DownloadHandler downloader;

    public TemplateManager(DownloadHandler handler){
        downloader = handler;
        handler.setManager(this);
    }

    public void clear(){
        downloader.clear();
    }

    public DownloadHandler getDownloadHandler(){
        return downloader;
    }

    @Nullable
    public abstract AbstractTemplate getTemplate(int templateID);

    public abstract int addTemplate(AbstractTemplate template);

    public static DownloadHandler getDownloadHandler(boolean isRemote){
        return isRemote ? Client.INSTANCE.getDownloadHandler() : Server.INSTANCE.getDownloadHandler();
    }

    public static TemplateManager getTemplateManager(boolean isRemote){
        return isRemote ? Client.INSTANCE : Server.INSTANCE;
    }

    public static class Client extends TemplateManager{

        public static TemplateManager.Client INSTANCE = new TemplateManager.Client();

        public Map<Integer, AbstractTemplate> templates = new HashMap<>();

        public Client() {
            super(new DownloadHandler.Client());
        }

        @Override
        public void clear() {
            super.clear();
            templates.clear();
        }

        @Nullable
        @Override
        public AbstractTemplate getTemplate(int templateID) {
            AbstractTemplate template = templates.get(templateID);
            if(template != null){
                return template;
            }
            if(templateID != -1){
                downloader.sendRequestPacket(templateID);
            }
            return null;
        }

        @Override
        public int addTemplate(AbstractTemplate template) {
            templates.put(template.templateID, template);
            return template.templateID;
        }
    }

    public static class Server extends TemplateManager{

        public static TemplateManager.Server INSTANCE = new TemplateManager.Server();

        public Server() {
            super(new DownloadHandler.Server());
        }

        @Nullable
        @Override
        public AbstractTemplate getTemplate(int templateID) {
            return TemplateServerData.get().templates.get(templateID);
        }

        @Override
        public int addTemplate(AbstractTemplate template) {
            template.templateID = TemplateServerData.get().getNextID();
            TemplateServerData.get().templates.put(template.templateID, template);
            TemplateServerData.get().markDirty();
            return template.templateID;
        }

    }

}
