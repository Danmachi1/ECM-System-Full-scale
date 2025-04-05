package com.ecmmanage.service;

import com.ecmmanage.ejb.WorkflowProcessorBean;
import com.ecmmanage.model.WorkflowInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkflowEJBServiceTest {

    @Mock
    private WorkflowProcessorBean workflowProcessorBean;

    @InjectMocks
    private WorkflowEJBService workflowEJBService;

    private WorkflowInstance workflowInstance;
    private List<String> steps;
    private List<String> approvalSteps;
    private List<String> automationSteps;

    @BeforeEach
    void setUp() {
        steps = Arrays.asList("Step 1", "Approval Step", "Final Step");
        approvalSteps = Arrays.asList("Approval Step");
        automationSteps = Arrays.asList("Automation Step 1");

        // Ensure the constructor matches the expected parameters
        workflowInstance = new WorkflowInstance("TestWorkflow", steps, approvalSteps, automationSteps);
    }

    @Test
    void testStartWorkflow() {
        when(workflowProcessorBean.startWorkflow(anyString(), anyList(), anyList(), anyList()))
                .thenReturn(workflowInstance);

        WorkflowInstance instance = workflowEJBService.startWorkflow("TestWorkflow", steps, approvalSteps, automationSteps);

        assertNotNull(instance);
        assertEquals("TestWorkflow", instance.getWorkflowName());
        verify(workflowProcessorBean, times(1)).startWorkflow(anyString(), anyList(), anyList(), anyList());
    }

    @Test
    void testApproveStep() {
        when(workflowProcessorBean.approveWorkflowStep(anyLong())).thenReturn(workflowInstance);

        WorkflowInstance instance = workflowEJBService.approveStep(1L);

        assertNotNull(instance);
        verify(workflowProcessorBean, times(1)).approveWorkflowStep(1L);
    }

    @Test
    void testCompleteWorkflow() {
        when(workflowProcessorBean.completeWorkflow(anyLong())).thenReturn(workflowInstance);

        WorkflowInstance instance = workflowEJBService.completeWorkflow(1L);

        assertNotNull(instance);
        verify(workflowProcessorBean, times(1)).completeWorkflow(1L);
    }

    @Test
    void testAutoProcessWorkflow() {
        when(workflowProcessorBean.autoProcessWorkflow(anyLong())).thenReturn(workflowInstance);

        WorkflowInstance instance = workflowEJBService.autoProcessWorkflow(1L);

        assertNotNull(instance);
        verify(workflowProcessorBean, times(1)).autoProcessWorkflow(1L);
    }
}
