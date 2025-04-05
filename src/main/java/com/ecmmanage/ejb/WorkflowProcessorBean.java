package com.ecmmanage.ejb;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import com.ecmmanage.model.WorkflowInstance;
import com.ecmmanage.repository.WorkflowInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ✅ Workflow Processor Bean
 * - Handles **workflow execution, approvals, and automation**.
 */
@Service
public class WorkflowProcessorBean {

    private static final Logger LOGGER = Logger.getLogger(WorkflowProcessorBean.class.getName());

    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;

    /**
     * ✅ Start a New Workflow
     * - Registers **workflow steps, approval steps, and automation conditions**.
     *
     * @param workflowName  The workflow name.
     * @param steps         List of workflow steps.
     * @param approvalSteps List of steps requiring approval.
     * @param automationSteps List of automated steps.
     * @return The created WorkflowInstance.
     */
    public WorkflowInstance startWorkflow(String workflowName, List<String> steps, List<String> approvalSteps, List<String> conditions) {
        LOGGER.info("Starting workflow: " + workflowName);
        
        WorkflowInstance instance = new WorkflowInstance(workflowName, steps, approvalSteps, conditions);
        return workflowInstanceRepository.save(instance);
    }


    /**
     * ✅ Approve a Workflow Step
     * - Moves to the **next step** if approval is granted.
     * - Marks workflow **Completed** if no more steps remain.
     *
     * @param workflowId The workflow ID.
     * @return The updated WorkflowInstance.
     */
    public WorkflowInstance approveWorkflowStep(Long workflowId) {
        WorkflowInstance instance = workflowInstanceRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("❌ Workflow not found"));

        // 🔹 Check if the current step requires approval
        if (instance.getApprovalRequiredSteps().contains(instance.getCurrentStep())) {
            LOGGER.info("✅ Approving step: " + instance.getCurrentStep());
            boolean moved = instance.moveToNextStep();

            if (!moved) {
                instance.setStatus("Completed");
                instance.setCompletedAt(LocalDateTime.now());
                LOGGER.info("✅ Workflow completed.");
            }
        } else {
            throw new RuntimeException("❌ Current step does not require approval.");
        }

        return workflowInstanceRepository.save(instance);
    }

    /**
     * ✅ Auto-Process Workflow
     * - **Automatically moves through non-approval steps**.
     * - **Stops** at the next approval-required step.
     *
     * @param workflowId The workflow ID.
     * @return The updated WorkflowInstance.
     */
    public WorkflowInstance autoProcessWorkflow(Long workflowId) {
        WorkflowInstance instance = workflowInstanceRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("❌ Workflow not found"));

        LOGGER.info("🔄 Auto-processing workflow: " + workflowId);

        // 🔹 Continue through steps until an **approval step** is reached
        while (!instance.getApprovalRequiredSteps().contains(instance.getCurrentStep())) {
            LOGGER.info("🔹 Auto-processing step: " + instance.getCurrentStep());
            boolean moved = instance.moveToNextStep();

            // 🔹 If no more steps remain, mark workflow **Completed**
            if (!moved) {
                instance.setStatus("Completed");
                instance.setCompletedAt(LocalDateTime.now());
                LOGGER.info("✅ Workflow completed.");
                break;
            }
        }

        return workflowInstanceRepository.save(instance);
    }

    /**
     * ✅ Cancel a Workflow
     * - Updates the workflow status to **Cancelled**.
     *
     * @param workflowId The workflow ID.
     * @return The updated WorkflowInstance.
     */
    public WorkflowInstance cancelWorkflow(Long workflowId) {
        WorkflowInstance instance = workflowInstanceRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("❌ Workflow not found"));

        instance.setStatus("Cancelled");
        LOGGER.info("❌ Workflow cancelled: " + workflowId);
        return workflowInstanceRepository.save(instance);
    }
    /**
     * ✅ Completes a workflow instance by marking it as "Completed".
     */
    public WorkflowInstance completeWorkflow(Long workflowId) {
        WorkflowInstance instance = workflowInstanceRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        LOGGER.info("Completing workflow: " + workflowId);
        instance.setStatus("Completed");
        instance.setCompletedAt(java.time.LocalDateTime.now());

        return workflowInstanceRepository.save(instance);
    }

}
