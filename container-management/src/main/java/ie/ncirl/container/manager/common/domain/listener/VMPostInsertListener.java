package ie.ncirl.container.manager.common.domain.listener;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

import org.springframework.beans.factory.annotation.Autowired;

import ie.ncirl.container.manager.app.service.VMClient;
import ie.ncirl.container.manager.common.domain.VM;

/**
 * This is Implementation of the Observer pattern.
 * The listener interface for receiving VMPostInsert events.
 * The class that is interested in processing a VMPostInsert
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addVMPostInsertListener<code> method. When
 * the VMPostInsert event occurs, that object's appropriate
 * method is invoked.
 */
/**
 * This Class Implements Observer Design Pattern
 * referring documentation provided by Jboss https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#events
 * **/
public class VMPostInsertListener {

    @Autowired
	private
	VMClient vmClient;

    /**
     * Configure VM.
     *
     * @param vm the vm
     */
    @PostUpdate
    @PostPersist
    public void configureVM(VM vm) {
        vmClient.configureVM(vm);
    }

}
