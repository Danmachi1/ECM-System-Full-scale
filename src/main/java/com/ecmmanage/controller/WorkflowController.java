package com.ecmmanage.controller;

import com.ecmmanage.ejb.WorkflowProcessorBean;
import com.ecmmanage.ejb.IBMWorkflowAdapter;
import com.ecmmanage.model.WorkflowInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * ✅ REST API Controller for Workflow Execution.
 * - Supports both **custom workflows** and **IBM-compatible workflows**.
 */
@RestController
@RequestMapping("/workflows")
public class WorkflowController {

    @Autowired
    private WorkflowProcessorBean workflowProcessorBean;

    @Autowired
    private IBMWorkflowAdapter ibmWorkflowAdapter; // ✅ IBM Workflow Adapter for File Uploads

    /**
     * ✅ Start a new workflow (Custom workflow).
     */
    @PostMapping("/start")
    public ResponseEntity<WorkflowInstance> startWorkflow(@RequestParam String workflowName) {
        List<String> steps = Arrays.asList("Step 1", "Approval Step", "Final Step");
        List<String> approvalSteps = Arrays.asList("Approval Step");
        List<String> conditions = null;
        WorkflowInstance instance = workflowProcessorBean.startWorkflow(workflowName, steps, approvalSteps, conditions);
        return ResponseEntity.ok(instance);
    }

    /**
     * ✅ Approve a workflow step.
     */
    @PostMapping("/approve/{workflowId}")
    public ResponseEntity<WorkflowInstance> approveStep(@PathVariable Long workflowId) {
        WorkflowInstance instance = workflowProcessorBean.approveWorkflowStep(workflowId);
        return ResponseEntity.ok(instance);
    }

    /**
     * ✅ Auto-process workflow steps.
     */
    @PostMapping("/auto-process/{workflowId}")
    public ResponseEntity<WorkflowInstance> autoProcess(@PathVariable Long workflowId) {
        WorkflowInstance instance = workflowProcessorBean.autoProcessWorkflow(workflowId);
        return ResponseEntity.ok(instance);
    }

    /**
     * ✅ Upload an IBM Workflow File and Convert It.
     */
    @PostMapping("/upload-ibm-workflow")
    public ResponseEntity<WorkflowInstance> uploadIBMWorkflow(@RequestParam("file") MultipartFile file) {
        WorkflowInstance instance = ibmWorkflowAdapter.uploadIBMWorkflowFile(file);
        return ResponseEntity.ok(instance);
    }
}
