package com.ecmmanage.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

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
    @OrderColumn(name = "step_index")
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
        this.steps = new ArrayList<>(steps != null ? steps : List.of());
        this.approvalSteps = new ArrayList<>(approvalSteps != null ? approvalSteps : List.of());
        this.automationSteps = new ArrayList<>(automationSteps != null ? automationSteps : List.of());
    }

    // ✅ Getters
    public Long getId() { return id; }
    public String getWorkflowName() { return workflowName; }
    public List<String> getSteps() { return steps; }
    public List<String> getApprovalSteps() { return approvalSteps; }
    public List<String> getAutomationSteps() { return automationSteps; }

    // ✅ Setters
    public void setWorkflowName(String workflowName) { this.workflowName = workflowName; }
    public void setSteps(List<String> steps) { this.steps = new ArrayList<>(steps != null ? steps : List.of()); }
    public void setApprovalSteps(List<String> approvalSteps) { this.approvalSteps = new ArrayList<>(approvalSteps != null ? approvalSteps : List.of()); }
    public void setAutomationSteps(List<String> automationSteps) { this.automationSteps = new ArrayList<>(automationSteps != null ? automationSteps : List.of()); }
}
