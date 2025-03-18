// Declares the package for the adapter.
package com.ecmmanage.ejb;

import java.util.logging.Logger; // Allows logging actions.

// Import necessary components.
import com.ecmmanage.model.WorkflowInstance;
import com.ecmmanage.repository.WorkflowInstanceRepository;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

/**
 * This class provides an adapter for IBM workflow compatibility.
 * It translates IBM workflow calls into our new system.
 */
@Stateless // Marks this class as a stateless EJB.
public class IBMWorkflowAdapter {

    // Logger to track workflow processing.
    private static final Logger LOGGER = Logger.getLogger(IBMWorkflowAdapter.class.getName());

    @Inject // Injects the workflow processing bean.
    private WorkflowProcessorBean workflowProcessorBean;

    @Inject // Injects the workflow repository.
    private WorkflowInstanceRepository workflowInstanceRepository;

    /**
     * Processes a workflow request using IBM-compatible rules.
     * 1. Starts a new workflow.
     * 2. Saves the workflow instance in the database.
     * 3. Retrieves and returns the saved workflow.
     *
     * @param workflowName The name of the workflow to process.
     * @return The stored workflow instance.
     */
    public WorkflowInstance processIBMWorkflow(String workflowName) {
        LOGGER.info("Processing IBM-Compatible Workflow: " + workflowName);

        // Start the workflow using the workflow processor.
        WorkflowInstance workflowInstance = workflowProcessorBean.startWorkflow(workflowName);

        // Save the workflow instance to the database.
        workflowInstanceRepository.save(workflowInstance);
        LOGGER.info("Workflow saved to database with ID: " + workflowInstance.getId());

        // Retrieve the stored instance from the database for verification.
        WorkflowInstance storedWorkflow = workflowInstanceRepository.findById(workflowInstance.getId()).orElse(null);
        if (storedWorkflow != null) {
            LOGGER.info("Successfully retrieved stored workflow: " + storedWorkflow.getWorkflowName());
        } else {
            LOGGER.warning("Failed to retrieve stored workflow.");
        }

        return storedWorkflow; // Return the saved workflow instance.
    }
}
