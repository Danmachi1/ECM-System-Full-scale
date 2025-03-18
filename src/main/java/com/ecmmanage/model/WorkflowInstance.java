// This package declaration ensures the class is inside the "model" package.
package com.ecmmanage.model;

import java.time.LocalDateTime; // Allows tracking of timestamps.

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * This class represents a workflow instance that is currently being processed.
 * Every time a document goes through a workflow, a new instance of this class is created.
 */
@Entity // Marks this class as a database entity.
@Table(name = "workflow_instances") // Maps this class to a table called "workflow_instances".
public class WorkflowInstance {

    @Id // Specifies this field as the primary key.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Enables auto-generation of IDs.
    private Long id; // Unique identifier for the workflow instance.

    @Column(name = "workflow_name", nullable = false) // The name of the workflow.
    private String workflowName;

    @Column(name = "status", nullable = false) // Stores the status of the workflow (e.g., "In Progress", "Completed").
    private String status;

    @Column(name = "started_at") // Timestamp for when the workflow started.
    private LocalDateTime startedAt;

    @Column(name = "completed_at") // Timestamp for when the workflow was completed.
    private LocalDateTime completedAt;

    /**
     * Default constructor (required by JPA).
     */
    public WorkflowInstance() {
        this.startedAt = LocalDateTime.now(); // Automatically set the start time.
        this.status = "In Progress"; // Default status is "In Progress".
    }

    /**
     * Constructor to create a workflow instance with a specific workflow name.
     */
    public WorkflowInstance(String workflowName) {
        this.workflowName = workflowName;
        this.startedAt = LocalDateTime.now();
        this.status = "In Progress";
    }

    // Getter and Setter methods to access or modify values.
    public Long getId() { return id; }
    public String getWorkflowName() { return workflowName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
