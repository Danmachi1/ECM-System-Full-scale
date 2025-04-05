package com.ecmmanage.service;

import com.ecmmanage.model.WorkflowDefinition;
import com.ecmmanage.model.WorkflowInstance;
import com.ecmmanage.repository.WorkflowDefinitionRepository;
import com.ecmmanage.repository.WorkflowInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkflowServiceTest {

    @Mock
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    @Mock
    private WorkflowInstanceRepository workflowInstanceRepository;

    @InjectMocks
    private WorkflowService workflowService;

    private WorkflowDefinition workflowDefinition;
    private List<String> steps;
    private List<String> approvalSteps;
    private List<String> automationSteps;

    @BeforeEach
    void setUp() {
        steps = Arrays.<String>asList("Step 1", "Approval Step", "Final Step");
        approvalSteps = Arrays.<String>asList("Approval Step");
        automationSteps = Arrays.<String>asList("Automation Step 1");

        // Ensure correct constructor usage for WorkflowDefinition
        workflowDefinition = new WorkflowDefinition("TestWorkflow", steps, approvalSteps, automationSteps);
    }

    @Test
    void testCreateWorkflow() {
        when(workflowDefinitionRepository.save(any(WorkflowDefinition.class))).thenReturn(workflowDefinition);

        WorkflowDefinition createdWorkflow = workflowService.createWorkflow("TestWorkflow", steps, approvalSteps, automationSteps);

        assertNotNull(createdWorkflow);
        assertEquals("TestWorkflow", createdWorkflow.getWorkflowName());
        verify(workflowDefinitionRepository, times(1)).save(any(WorkflowDefinition.class));
    }

    @Test
    void testStartWorkflow() {
        when(workflowDefinitionRepository.findByWorkflowName(anyString())).thenReturn(Optional.of(workflowDefinition));
        when(workflowInstanceRepository.save(any(WorkflowInstance.class)))
                .thenReturn(new WorkflowInstance("TestWorkflow", steps, approvalSteps, automationSteps));

        WorkflowInstance startedInstance = workflowService.startWorkflow("TestWorkflow");

        assertNotNull(startedInstance);
        assertEquals("TestWorkflow", startedInstance.getWorkflowName());
        verify(workflowInstanceRepository, times(1)).save(any(WorkflowInstance.class));
    }
}
