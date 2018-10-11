package jmri.jmrit.newlogix.log;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import jmri.jmrit.newlogix.log.Log.UnsupportedVersionException;
import jmri.jmrit.newlogix.log.Log.InvalidFormatException;

/**
 * The header of the log, including the list of items.
 * The header must always be written using UTF-8, StandardCharsets.UTF_8.
 * '\n', ascii 12, is used as new line separator.
 */
public class LogHeader {
    
    /**
     * The maximum length of a string in the header.
     */
    public static final int MAX_LINE_LENGTH = 2048;
    
    /**
     * The top most version supported by this header class.
     */
    private static final int SUPPORTED_VERSION = 1;
    
    private final Log _newLogixLog;
    private Encodings _encoding = Encodings.ASCII_ONE_BIT_PER_CHAR;
    private String _name = "";
    private int _version = SUPPORTED_VERSION;
    
    public LogHeader(Log newLogixLog) {
        _newLogixLog = newLogixLog;
    }
    
    /**
     * Set the encoding of this log.
     * @param encoding the encoding
     */
    public void setEncoding(Encodings encoding) {
        _encoding = encoding;
    }
    
    /**
     * Get the encoding of this log.
     * @return the encoding
     */
    public Encodings getEncoding() {
        return _encoding;
    }
    
    /**
     * Set the name of this log.
     * @param name the name
     */
    public void setName(String name) {
        _name = name;
    }
    
    /**
     * Get the name of this log.
     * @return the name
     */
    public String getName() {
        return _name;
    }
    
    private void readHeaderLines(StreamReader reader) throws IOException, InvalidFormatException, UnsupportedVersionException {
        String line;
        while ((line = reader.readLine('[')) != null) {
            String[] parts = line.split("=", 2);
            
            switch (parts[0]) {
                case "version":
                    _version = Integer.parseInt(parts[1]);
                    if (_version > SUPPORTED_VERSION) {
                        throw new UnsupportedVersionException(
                                String.format("Version %d of the log is not supported." +
                                              " Only versions up to and including version %d are supported.",
                                        _version, SUPPORTED_VERSION));
                    }
                    break;
                case "name":
                    _name = parts[1];
                    break;
                case "encoding":
                    _encoding = Encodings.getEncodingFromName(parts[1]);
                    break;
                default:
                    throw new InvalidFormatException(String.format("String '%s' is unknown or misplaced in log", line));
            }
        }
    }
    
    private void readItemLines(StreamReader reader) throws IOException,
            InvalidFormatException {
        
        _newLogixLog.clearItemList();
        
        String line;
        while ((line = reader.readLine('[')) != null) {
            _newLogixLog.addItem(line);
        }
    }
    
    /**
     * Read the header of this log from an InputStream.
     * @param input the input stream to read from
     * @throws IOException
     * @throws jmri.jmrit.newlogix.log.Log.InvalidFormatException
     * @throws jmri.jmrit.newlogix.log.Log.UnsupportedVersionException 
     */
    public void readHeader(InputStream input) throws IOException,
            InvalidFormatException, UnsupportedVersionException {
        StreamReader reader = new StreamReader(input);
        String line;
        while ((line = reader.readLine()) != null) {
            switch (line) {
                case "[header]":
                    readHeaderLines(reader);
                    break;
                case "[items]":
                    readItemLines(reader);
                    break;
                case "[data]":
                    // We are finished in this method.
                    return;
                default:
                    System.out.format("Line: '%s'%n", line);
                    throw new InvalidFormatException(String.format("String '%s' is unknown or misplaced in log", line));
            }
        }
        
        // If we are here, we haven't found section '[data]' yet.
        throw new InvalidFormatException("Section [data] is missing. End of stream reached.");
    }
    
    /**
     * Write the header of this log to an OutputStream.
     * @param output the stream to write to
     * @throws IOException 
     */
    @SuppressFBWarnings(value = "VA_FORMAT_STRING_USES_NEWLINE",
            justification = "the log file uses \n as new line character which simplifies parsing")
    public void writeHeader(OutputStream output) throws IOException {
        // Note: '\n', ascii 12, must be used as new line separator.
        
        // Write header
        output.write("[header]\n".getBytes(StandardCharsets.UTF_8));
        output.write(String.format("version=%d\n", SUPPORTED_VERSION).getBytes(StandardCharsets.UTF_8));
        output.write(String.format("encoding=%s\n", _encoding.getName()).getBytes(StandardCharsets.UTF_8));
        output.write(String.format("name=%s\n", _name).getBytes(StandardCharsets.UTF_8));
        
        // Write the system names of the items
        output.write("[items]\n".getBytes(StandardCharsets.UTF_8));
        for (String item : _newLogixLog.getItemList()) {
            output.write(String.format("%s\n", item).getBytes(StandardCharsets.UTF_8));
        }
        
        // Write data header
        output.write("[data]\n".getBytes(StandardCharsets.UTF_8));
    }
    
    
    
    private static class StreamReader {
        
        private static final int NO_CHAR = -2;
        
        private final InputStream _input;
        private int _ch;
        
        StreamReader(InputStream input) {
            _input = input;
            _ch = NO_CHAR;
        }
        
        
        private String readLine() throws IOException {
            // -1 if end of stream and in that case we will abort anyway
            return readLine(-1);
        }
        
        private String readLine(int abortChar) throws IOException {
            byte[] buffer = new byte[MAX_LINE_LENGTH];
            int length = 0;
            if (_ch == NO_CHAR) {
                _ch = _input.read();
            }
            while ((_ch != -1) && (_ch != '\n') && (_ch != abortChar)) {
                buffer[length++] = (byte)_ch;
                _ch = _input.read();
            }
            if (_ch == '\n') {
                _ch = NO_CHAR;
            }
            if (length > 0) {
                return new String(buffer, 0, length, StandardCharsets.UTF_8);
            } else {
                // End of stream
                return null;
            }
        }
        
    }
    
}
