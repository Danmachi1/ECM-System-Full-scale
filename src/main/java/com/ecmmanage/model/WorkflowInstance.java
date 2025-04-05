package com.ecmmanage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ✅ Represents a workflow execution instance.
 * - Tracks workflow progress in real-time.
 * - Supports approvals, conditions, and automation.
 */
@Entity
@Table(name = "workflow_instances")
public class WorkflowInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String workflowName;
    private String currentStep;
    private String status;  // Pending, Approved, Rejected, Completed
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @ElementCollection
    private List<String> workflowSteps; // 🔹 Defines the sequence of steps.

    @ElementCollection
    private List<String> approvalRequiredSteps; // 🔹 Steps that need explicit approval.

    @ElementCollection
    private List<String> automationSteps; // 🔹 Steps that are auto-processed.

    public WorkflowInstance() {}

    public WorkflowInstance(String workflowName, List<String> workflowSteps, List<String> approvalRequiredSteps, List<String> automationSteps) {
        if (workflowSteps == null || workflowSteps.isEmpty()) {
            throw new IllegalArgumentException("Workflow steps cannot be empty.");
        }
        this.workflowName = workflowName;
        this.workflowSteps = workflowSteps;
        this.approvalRequiredSteps = approvalRequiredSteps != null ? approvalRequiredSteps : List.of();
        this.automationSteps = automationSteps != null ? automationSteps : List.of();
        this.currentStep = workflowSteps.get(0);
        this.status = "Pending";
        this.startedAt = LocalDateTime.now();
    }

    // ✅ Getters and Setters
    public Long getId() { return id; }
    public String getWorkflowName() { return workflowName; }
    public String getCurrentStep() { return currentStep; }
    public String getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public List<String> getWorkflowSteps() { return workflowSteps; }
    public List<String> getApprovalRequiredSteps() { return approvalRequiredSteps; }
    public List<String> getAutomationSteps() { return automationSteps; }

    public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
    public void setStatus(String status) { this.status = status; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    /**
     * ✅ Moves to the next step if conditions are met.
     * - **Auto-processes steps if they are automation steps.**
     * - **Waits for approval if the next step requires it.**
     */
    public boolean moveToNextStepWithConditions() {
        int index = workflowSteps.indexOf(currentStep);
        if (index < 0 || index >= workflowSteps.size() - 1) {
            this.status = "Completed";
            this.completedAt = LocalDateTime.now();
            return false;
        }
        
        String nextStep = workflowSteps.get(index + 1);
        
        if (approvalRequiredSteps.contains(nextStep)) {
            this.status = "Awaiting Approval";
        } else if (automationSteps.contains(nextStep)) {
            this.status = "Auto-Processing";
            this.currentStep = nextStep;
            return moveToNextStepWithConditions(); // Recursive call for auto-processing.
        } else {
            this.currentStep = nextStep;
        }
        return true;
    }

    /**
     * ✅ Approves the current step and moves forward.
     */
    public boolean approveCurrentStep() {
        if (!approvalRequiredSteps.contains(currentStep)) {
            throw new IllegalStateException("Current step does not require approval.");
        }
        this.status = "Approved";
        return moveToNextStepWithConditions();
    }

    /**
     * ✅ Moves to the next step normally (without conditions).
     */
    public boolean moveToNextStep() {
        int index = workflowSteps.indexOf(currentStep);
        if (index < 0 || index >= workflowSteps.size() - 1) {
            this.status = "Completed";
            this.completedAt = LocalDateTime.now();
            return false;
        }
        this.currentStep = workflowSteps.get(index + 1);
        return true;
    }
}
