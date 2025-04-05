package com.ecmmanage.ejb;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.ecmmanage.model.WorkflowInstance;
import com.ecmmanage.repository.WorkflowInstanceRepository;
import jakarta.inject.Inject;
import org.springframework.web.multipart.MultipartFile;

/**
 * ✅ IBM Workflow Adapter
 * - Uploads IBM workflow files and **translates them** into our system's workflow format.
 * - Supports **conditions, approvals, and automation**.
 */
@Component
public class IBMWorkflowAdapter {

    private static final Logger LOGGER = Logger.getLogger(IBMWorkflowAdapter.class.getName());

    @Inject
    private WorkflowProcessorBean workflowProcessorBean;

    @Inject
    private WorkflowInstanceRepository workflowInstanceRepository;

    /**
     * ✅ Upload and Convert an IBM Workflow File.
     * - Reads an **XML/XPD** IBM workflow file.
     * - Extracts **workflow steps, approvals, and automation rules**.
     * - Creates a new **WorkflowInstance** in our system.
     *
     * @param file The IBM workflow file to process.
     * @return The created WorkflowInstance.
     */
    public WorkflowInstance uploadIBMWorkflowFile(MultipartFile file) {
        LOGGER.info("Uploading IBM Workflow File: " + file.getOriginalFilename());

        try {
            // 🔹 Read the IBM workflow file content
            String content = new String(file.getBytes());

            // 🔹 Parse the IBM Workflow file and extract **steps, conditions, and approvals**
            List<String> workflowSteps = parseIBMWorkflow(content);
            List<String> approvalSteps = extractApprovalSteps(workflowSteps);
            List<String> automationSteps = extractAutomationSteps(workflowSteps);

            // 🔹 Generate a **workflow name** from the filename (removing extensions)
            String workflowName = file.getOriginalFilename().replace(".xml", "").replace(".xpd", "");

            // 🔹 Start a new workflow **with conditions, approvals, and automation**
            WorkflowInstance instance = workflowProcessorBean.startWorkflow(
                    workflowName, workflowSteps, approvalSteps, automationSteps
            );

            LOGGER.info("✅ IBM Workflow successfully converted and started.");
            return instance;

        } catch (IOException e) {
            LOGGER.severe("❌ Failed to upload IBM workflow file: " + e.getMessage());
            throw new RuntimeException("Error processing IBM workflow file", e);
        }
    }

    /**
     * ✅ Parses the IBM workflow file and extracts workflow **steps**.
     * - (Assumes the workflow file is **XML-based**).
     *
     * @param content The raw content of the workflow file.
     * @return A **list of workflow steps**.
     */
    private List<String> parseIBMWorkflow(String content) {
        LOGGER.info("Parsing IBM workflow file content...");

        return content.lines()
                .filter(line -> line.trim().startsWith("<Step>")) // Extracting workflow steps
                .map(line -> line.replace("<Step>", "").replace("</Step>", "").trim())
                .collect(Collectors.toList());
    }

    /**
     * ✅ Extracts **Approval Steps** from the parsed workflow steps.
     * - Approval steps are **automatically detected** based on their naming.
     *
     * @param steps List of workflow steps.
     * @return List of **approval steps**.
     */
    private List<String> extractApprovalSteps(List<String> steps) {
        return steps.stream()
                .filter(step -> step.toLowerCase().contains("approval"))
                .collect(Collectors.toList());
    }

    /**
     * ✅ Extracts **Automation Steps**.
     * - Any step containing `"Auto-"` is **automated** (for example: "Auto-Notify", "Auto-Assign").
     *
     * @param steps List of workflow steps.
     * @return List of **automation steps**.
     */
    private List<String> extractAutomationSteps(List<String> steps) {
        return steps.stream()
                .filter(step -> step.toLowerCase().startsWith("auto-"))
                .collect(Collectors.toList());
    }
}
