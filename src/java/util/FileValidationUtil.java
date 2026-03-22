package util;

import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Server-side file type validation using magic bytes (no external JAR needed).
 * Checks both file extension whitelist and actual file content signatures.
 */
public class FileValidationUtil {

    private static final Logger logger = Logger.getLogger(FileValidationUtil.class.getName());

    // Allowed extensions (lowercase)
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
        "pdf", "jpg", "jpeg", "png", "doc", "docx", "zip", "rar", "txt", "pptx", "xlsx"
    ));

    // Blocked extensions
    private static final Set<String> BLOCKED_EXTENSIONS = new HashSet<>(Arrays.asList(
        "exe", "bat", "cmd", "sh", "php", "js", "jar", "class", "py", "rb", "pl"
    ));

    // Allowed MIME types
    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(Arrays.asList(
        "application/pdf",
        "image/jpeg",
        "image/png",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "application/zip",
        "application/x-zip-compressed",
        "text/plain"
    ));

    /**
     * Check if file extension is allowed.
     */
    public static boolean isAllowedExtension(String filename) {
        if (filename == null) return false;
        String ext = getExtension(filename).toLowerCase();
        if (BLOCKED_EXTENSIONS.contains(ext)) return false;
        return ALLOWED_EXTENSIONS.contains(ext);
    }

    /**
     * Detect MIME type from file magic bytes (first 8 bytes).
     */
    public static String detectMimeType(InputStream inputStream) throws IOException {
        byte[] header = new byte[8];
        int read = inputStream.read(header);
        if (read < 4) return "application/octet-stream";

        // PDF: %PDF
        if (header[0] == 0x25 && header[1] == 0x50 && header[2] == 0x44 && header[3] == 0x46) {
            return "application/pdf";
        }
        // JPEG: FF D8 FF
        if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        // PNG: 89 50 4E 47
        if ((header[0] & 0xFF) == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47) {
            return "image/png";
        }
        // ZIP / DOCX / XLSX / PPTX: PK (50 4B 03 04)
        if (header[0] == 0x50 && header[1] == 0x4B && header[2] == 0x03 && header[3] == 0x04) {
            return "application/zip";  // Could be docx/xlsx/pptx (all ZIP-based)
        }
        // Old MS Office (DOC/XLS/PPT): D0 CF 11 E0
        if ((header[0] & 0xFF) == 0xD0 && (header[1] & 0xFF) == 0xCF &&
            (header[2] & 0xFF) == 0x11 && (header[3] & 0xFF) == 0xE0) {
            return "application/msword";
        }
        // Plain text heuristic (all printable ASCII in first 8 bytes)
        boolean allPrintable = true;
        for (int i = 0; i < read; i++) {
            int b = header[i] & 0xFF;
            if (b < 0x09 || (b > 0x0D && b < 0x20) || b == 0x7F) {
                allPrintable = false;
                break;
            }
        }
        if (allPrintable) return "text/plain";

        return "application/octet-stream";
    }

    /**
     * Check if detected MIME type is allowed.
     */
    public static boolean isAllowedMimeType(String mimeType) {
        return ALLOWED_MIME_TYPES.contains(mimeType);
    }

    /**
     * Full validation: extension + MIME type.
     * @return null if valid, error message if rejected
     */
    public static String validate(String filename, InputStream inputStream) {
        // 1. Extension check
        if (!isAllowedExtension(filename)) {
            String msg = "Loại file không được phép. Chỉ chấp nhận: PDF, Word, ảnh (JPG/PNG), ZIP.";
            logger.warning("[BLOCKED UPLOAD] file=" + filename + " reason=blocked_extension");
            return msg;
        }

        // 2. MIME type check (magic bytes)
        try {
            String detectedType = detectMimeType(inputStream);
            if (!isAllowedMimeType(detectedType)) {
                String msg = "Loại file không được phép. Chỉ chấp nhận: PDF, Word, ảnh (JPG/PNG), ZIP.";
                logger.warning("[BLOCKED UPLOAD] file=" + filename + " mime=" + detectedType);
                return msg;
            }
        } catch (IOException e) {
            logger.warning("[UPLOAD ERROR] file=" + filename + " error=" + e.getMessage());
            return "Không thể đọc file. Vui lòng thử lại.";
        }

        return null; // valid
    }

    private static String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot == -1 || dot == filename.length() - 1) return "";
        return filename.substring(dot + 1);
    }
}
