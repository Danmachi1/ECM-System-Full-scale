package com.ecmmanage.service;

import com.ecmmanage.ejb.WorkflowProcessorBean;
import com.ecmmanage.model.WorkflowInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ✅ Service class that interacts with the EJB workflow processor.
 */
@Service
public class WorkflowEJBService {

    @Autowired // ✅ Injects the EJB workflow processor.
    private WorkflowProcessorBean workflowProcessorBean;

    /**
     * ✅ Starts a new workflow instance with predefined steps and approvals.
     */
    public WorkflowInstance startWorkflow(String workflowName, List<String> steps, List<String> approvalSteps, List<String> conditions) {
        return workflowProcessorBean.startWorkflow(workflowName, steps, approvalSteps, conditions);
    }

    /**
     * ✅ Approves the current step in a workflow.
     */
    public WorkflowInstance approveStep(Long workflowId) {
        return workflowProcessorBean.approveWorkflowStep(workflowId);
    }

    /**
     * ✅ Completes a workflow instance by marking it as "Completed".
     */
    public WorkflowInstance completeWorkflow(Long workflowId) {
        return workflowProcessorBean.completeWorkflow(workflowId);
    }

    /**
     * ✅ Auto-processes all steps that do not require approval.
     */
    public WorkflowInstance autoProcessWorkflow(Long workflowId) {
        return workflowProcessorBean.autoProcessWorkflow(workflowId);
    }
}
