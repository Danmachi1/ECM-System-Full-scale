package com.ecmmanage.ejb;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import com.ecmmanage.model.WorkflowInstance;
import com.ecmmanage.repository.WorkflowInstanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ✅ Converts EJB to a Spring service.
 * This class manages workflow execution.
 */
@Service // 🚀 Replaces @Stateless with @Service so Spring can manage this class.
public class WorkflowProcessorBean {

    private static final Logger LOGGER = Logger.getLogger(WorkflowProcessorBean.class.getName());

    @Autowired // ✅ Spring injects the repository.
    private WorkflowInstanceRepository workflowInstanceRepository;

    /**
     * ✅ Starts a new workflow instance.
     */
    public WorkflowInstance startWorkflow(String workflowName) {
        LOGGER.info("Starting workflow: " + workflowName);
        WorkflowInstance instance = new WorkflowInstance(workflowName);
        return workflowInstanceRepository.save(instance);
    }

    /**
     * ✅ Completes a workflow instance by marking it as "Completed".
     */
    public WorkflowInstance completeWorkflow(Long workflowId) {
        WorkflowInstance instance = workflowInstanceRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        instance.setStatus("Completed");
        instance.setCompletedAt(LocalDateTime.now());
        return workflowInstanceRepository.save(instance);
    }
}
