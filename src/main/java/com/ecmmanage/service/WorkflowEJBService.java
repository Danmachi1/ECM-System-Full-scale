package com.ecmmanage.service;

import com.ecmmanage.ejb.WorkflowProcessorBean;
import com.ecmmanage.model.WorkflowInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class that interacts with the EJB workflow processor.
 */
@Service
public class WorkflowEJBService {

    @Autowired // Injects the EJB workflow processor.
    private WorkflowProcessorBean workflowProcessorBean;

    /**
     * Starts a new workflow instance.
     */
    public WorkflowInstance startWorkflow(String workflowName) {
        return workflowProcessorBean.startWorkflow(workflowName);
    }

    /**
     * Marks a workflow as completed.
     */
    public WorkflowInstance completeWorkflow(Long workflowId) {
        return workflowProcessorBean.completeWorkflow(workflowId);
    }
}
