package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowGroupEvent;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowGroup implements ToolWindowGroup {
    protected MyDoggyToolWindowManager manager;

    protected String name;
    protected List<ToolWindow> tools;
    protected EventListenerList listenerList;
    protected boolean implicit;
    protected boolean tempGroup;
    protected boolean activeteTool;


    public MyDoggyToolWindowGroup(MyDoggyToolWindowManager manager, String name, boolean tempGroup) {
        this.manager = manager;
        this.name = name;
        this.tools = new ArrayList<ToolWindow>();
        this.listenerList = new EventListenerList();
        this.implicit = false;
        this.tempGroup = tempGroup;
        this.activeteTool = true;
    }


    public String getName() {
        return name;
    }

    public void addToolWindow(ToolWindow toolWindow) {
        if (toolWindow == null)
            throw new NullPointerException("ToolWindow cannot be null.");

        if (!tools.contains(toolWindow)) {
            tools.add(toolWindow);
            fireAddedTool(toolWindow);
        } else
            throw new IllegalArgumentException("This group already contains passed tool window. [tool id : " + toolWindow.getId() + ", group : " + name + "]");
    }

    public boolean removeToolWindow(ToolWindow toolWindow) {
        if (toolWindow == null)
            throw new NullPointerException("ToolWindow cannot be null.");

        boolean removed = tools.remove(toolWindow);
        if (removed)
            fireRemovedTool(toolWindow);
        return removed;
    }

    public ToolWindow[] getToolsWindow() {
        return tools.toArray(new ToolWindow[tools.size()]);
    }

    public boolean containesToolWindow(ToolWindow toolWindow) {
        return tools.contains(toolWindow);
    }

    public void setImplicit(boolean implicit) {
        this.implicit = implicit;
    }

    public boolean isImplicit() {
        return implicit;
    }

    public void setVisible(final boolean visible) {
        if (tempGroup || manager.containsGroup(name)) {
            synchronized (manager.sync) {

                boolean doAction = false;
                for (ToolWindow tool : getToolsWindow()) {
                    if (tool.isVisible() != visible) {
                        doAction = true;
                        break;
                    }
                }

                if (doAction) {
                    for (ToolWindow tool : manager.getToolWindows()) {
                        if (tool.getType() != ToolWindowType.EXTERN)
                            tool.setVisible(false);
                    }

                    if (visible) {
                        manager.setShowingGroup(this);
                        try {
                            showTool(ToolWindowAnchor.LEFT);
                            showTool(ToolWindowAnchor.TOP);
                            showTool(ToolWindowAnchor.RIGHT);
                            showTool(ToolWindowAnchor.BOTTOM);
                        } finally {
                            manager.setShowingGroup(null);
                        }
                    }

                    if (visible)
                        fireGroupShown();
                    else
                        fireGroupHidden();
                }

                if (activeteTool && visible && tools.size() > 0)
                    tools.get(0).setActive(true);
            }
        }
    }

    public void addToolWindowGroupListener(ToolWindowGroupListener listener) {
        if (listener == null)
            return;

        listenerList.add(ToolWindowGroupListener.class, listener);
    }

    public void removeToolWindowGroupListener(ToolWindowGroupListener listener) {
        if (listener == null)
            return;

        listenerList.remove(ToolWindowGroupListener.class, listener);
    }

    public ToolWindowGroupListener[] getToolWindowGroupListeners() {
        return listenerList.getListeners(ToolWindowGroupListener.class);
    }

    public String toString() {
        return "MyDoggyToolWindowGroup{" +
               "name='" + name + '\'' +
               ", tools=" + tools +
               '}';
    }


    public boolean isActiveteTool() {
        return activeteTool;
    }

    public void setActiveteTool(boolean activeteTool) {
        this.activeteTool = activeteTool;
    }

    protected void fireGroupShown() {
        ToolWindowGroupEvent event = new ToolWindowGroupEvent(manager, ToolWindowGroupEvent.ActionId.GROUP_SHOWN, this);
        for (ToolWindowGroupListener listener : listenerList.getListeners(ToolWindowGroupListener.class)) {
            listener.groupShown(event);
        }
    }

    protected void fireGroupHidden() {
        ToolWindowGroupEvent event = new ToolWindowGroupEvent(manager, ToolWindowGroupEvent.ActionId.GROUP_HIDDEN, this);
        for (ToolWindowGroupListener listener : listenerList.getListeners(ToolWindowGroupListener.class)) {
            listener.groupHidden(event);
        }
    }

    protected void fireAddedTool(ToolWindow toolWindow) {
        ToolWindowGroupEvent event = new ToolWindowGroupEvent(manager, ToolWindowGroupEvent.ActionId.TOOL_ADDED, this, toolWindow);
        for (ToolWindowGroupListener listener : listenerList.getListeners(ToolWindowGroupListener.class)) {
            listener.toolAdded(event);
        }
    }

    protected void fireRemovedTool(ToolWindow toolWindow) {
        ToolWindowGroupEvent event = new ToolWindowGroupEvent(manager, ToolWindowGroupEvent.ActionId.TOOL_REMOVED, this, toolWindow);
        for (ToolWindowGroupListener listener : listenerList.getListeners(ToolWindowGroupListener.class)) {
            listener.toolRemoved(event);
        }
    }

    protected void showTool(ToolWindowAnchor anchor) {
        for (ToolWindow tool : getToolsWindow()) {
            if (tool.getAnchor() != anchor)
                continue;
            if (tool.getType() == ToolWindowType.SLIDING)
                tool.setType(ToolWindowType.DOCKED);

            tool.aggregate();
        }
    }

}
