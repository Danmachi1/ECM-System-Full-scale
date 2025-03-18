package com.ecmmanage.service;

import com.ecmmanage.model.WorkflowInstance;
import com.ecmmanage.repository.WorkflowInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service class for handling workflow operations.
 */
@Service
public class WorkflowService {

    @Autowired // Injects the repository to access workflow instances.
    private WorkflowInstanceRepository workflowInstanceRepository;

    /**
     * Saves a new workflow instance.
     */
    public WorkflowInstance saveWorkflow(WorkflowInstance workflowInstance) {
        return workflowInstanceRepository.save(workflowInstance);
    }

    /**
     * Retrieves all workflow instances.
     */
    public List<WorkflowInstance> getAllWorkflows() {
        return workflowInstanceRepository.findAll();
    }
}
