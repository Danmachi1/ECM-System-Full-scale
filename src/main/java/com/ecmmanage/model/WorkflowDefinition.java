package com.ecmmanage.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * ✅ Represents a workflow definition.
 * - Allows users to define new workflows dynamically.
 * - Supports **conditions, approvals, and automation**.
 */
@Entity
@Table(name = "workflow_definitions")
public class WorkflowDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workflow_name", nullable = false, unique = true)
    private String workflowName;

    @ElementCollection
    @CollectionTable(name = "workflow_steps", joinColumns = @JoinColumn(name = "workflow_id"))
    @Column(name = "step_name")
    private List<String> steps; // 🔹 General workflow steps.

    @ElementCollection
    @CollectionTable(name = "workflow_approval_steps", joinColumns = @JoinColumn(name = "workflow_id"))
    @Column(name = "approval_step_name")
    private List<String> approvalSteps; // 🔹 Steps requiring approval.

    @ElementCollection
    @CollectionTable(name = "workflow_auto_steps", joinColumns = @JoinColumn(name = "workflow_id"))
    @Column(name = "auto_step_name")
    private List<String> automationSteps; // 🔹 Steps that should be auto-processed.

    public WorkflowDefinition() {}

    public WorkflowDefinition(String workflowName, List<String> steps, List<String> approvalSteps, List<String> automationSteps) {
        this.workflowName = workflowName;
        this.steps = steps;
        this.approvalSteps = approvalSteps;
        this.automationSteps = automationSteps;
    }

    // ✅ Getters
    public Long getId() { return id; }
    public String getWorkflowName() { return workflowName; }
    public List<String> getSteps() { return steps; }
    public List<String> getApprovalSteps() { return approvalSteps; }
    public List<String> getAutomationSteps() { return automationSteps; }

    // ✅ Setters
    public void setWorkflowName(String workflowName) { this.workflowName = workflowName; }
    public void setSteps(List<String> steps) { this.steps = steps; }
    public void setApprovalSteps(List<String> approvalSteps) { this.approvalSteps = approvalSteps; }
    public void setAutomationSteps(List<String> automationSteps) { this.automationSteps = automationSteps; }
}
