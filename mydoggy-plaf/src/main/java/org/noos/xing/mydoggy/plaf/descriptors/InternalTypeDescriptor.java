package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface InternalTypeDescriptor {

    ToolWindowTypeDescriptor cloneMe(ToolWindowDescriptor toolWindowDescriptor);

}
