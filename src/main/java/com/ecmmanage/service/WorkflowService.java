package com.ecmmanage.service;

import com.ecmmanage.model.WorkflowDefinition;
import com.ecmmanage.model.WorkflowInstance;
import com.ecmmanage.repository.WorkflowDefinitionRepository;
import com.ecmmanage.repository.WorkflowInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * ✅ Handles **workflow execution** and management.
 */
@Service
public class WorkflowService {

    private static final Logger LOGGER = Logger.getLogger(WorkflowService.class.getName());

    @Autowired
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;

    /**
     * ✅ Creates a new workflow definition.
     */
    public WorkflowDefinition createWorkflow(String workflowName, List<String> steps, List<String> approvalSteps, List<String> automationSteps) {
        WorkflowDefinition workflow = new WorkflowDefinition(workflowName, steps, approvalSteps, automationSteps);
        return workflowDefinitionRepository.save(workflow);
    }

    /**
     * ✅ Starts a new workflow instance.
     */
    public WorkflowInstance startWorkflow(String workflowName) {
        Optional<WorkflowDefinition> definitionOpt = workflowDefinitionRepository.findByWorkflowName(workflowName);
        if (definitionOpt.isEmpty()) {
            throw new RuntimeException("Workflow definition not found: " + workflowName);
        }

        WorkflowDefinition definition = definitionOpt.get();
        List<String> steps = definition.getSteps();

        if (steps.isEmpty()) {
            throw new RuntimeException("Workflow definition must have at least one step.");
        }

        // 🔹 Identify approval steps (contains "Approval" keyword)
        List<String> approvalSteps = steps.stream()
                .filter(step -> step.toLowerCase().contains("approval"))
                .toList();

        // 🔹 Identify automation steps (contains "Auto" keyword)
        List<String> automationSteps = steps.stream()
                .filter(step -> step.toLowerCase().contains("auto"))
                .toList();

        // 🔹 Initialize a new workflow instance
        WorkflowInstance instance = new WorkflowInstance(workflowName, steps, approvalSteps, automationSteps);

        LOGGER.info("Started new workflow: " + workflowName);
        return workflowInstanceRepository.save(instance);
    }

    /**
     * ✅ Completes the **current step** and moves to the next step.
     */
    public void completeStep(Long workflowId) {
        WorkflowInstance instance = workflowInstanceRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow instance not found"));

        WorkflowDefinition definition = workflowDefinitionRepository.findByWorkflowName(instance.getWorkflowName())
                .orElseThrow(() -> new RuntimeException("Workflow definition not found"));

        List<String> steps = definition.getSteps();
        int currentStepIndex = steps.indexOf(instance.getCurrentStep());

        if (currentStepIndex == -1) {
            throw new RuntimeException("Current step not found in workflow definition");
        }

        // 🔹 Check if it's the final step
        if (currentStepIndex == steps.size() - 1) {
            instance.setStatus("COMPLETED");
            instance.setCompletedAt(LocalDateTime.now());
            LOGGER.info("Workflow completed: " + instance.getWorkflowName());
        } else {
            String nextStep = steps.get(currentStepIndex + 1);

            // 🔹 Check if next step requires approval
            if (instance.getApprovalRequiredSteps().contains(nextStep)) {
                instance.setStatus("AWAITING_APPROVAL");
                LOGGER.info("Next step requires approval: " + nextStep);
            } else {
                instance.setCurrentStep(nextStep);
                instance.setStatus("IN_PROGRESS");
                LOGGER.info("Moved to next step: " + nextStep);
            }
        }

        workflowInstanceRepository.save(instance);
    }

    /**
     * ✅ Approves the current workflow step.
     */
    public void approveStep(Long workflowId) {
        WorkflowInstance instance = workflowInstanceRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow instance not found"));

        if (!instance.getApprovalRequiredSteps().contains(instance.getCurrentStep())) {
            throw new RuntimeException("Current step does not require approval.");
        }

        LOGGER.info("Approving step: " + instance.getCurrentStep());

        instance.setStatus("APPROVED");
        completeStep(workflowId); // Move to the next step
    }

    /**
     * ✅ Auto-processes all steps that do not require approval.
     */
    public void autoProcessWorkflow(Long workflowId) {
        WorkflowInstance instance = workflowInstanceRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow instance not found"));

        LOGGER.info("Auto-processing workflow: " + workflowId);

        while (!instance.getApprovalRequiredSteps().contains(instance.getCurrentStep())) {
            completeStep(workflowId);
            if ("COMPLETED".equals(instance.getStatus())) {
                break;
            }
        }
    }
}
