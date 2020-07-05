package jmri.jmrix.oaktree.packetgen;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import jmri.jmrix.oaktree.SerialMessage;
import jmri.jmrix.oaktree.SerialReply;
import jmri.jmrix.oaktree.OakTreeSystemConnectionMemo;
import jmri.util.StringUtil;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Frame for user input of serial messages.
 *
 * @author Bob Jacobsen Copyright (C) 2002, 2003, 2006
 */
@API(status = EXPERIMENTAL)
public class SerialPacketGenFrame extends jmri.util.JmriJFrame implements jmri.jmrix.oaktree.SerialListener {

    // member declarations
    javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    javax.swing.JButton sendButton = new javax.swing.JButton();
    javax.swing.JTextField packetTextField = new javax.swing.JTextField(12);

    javax.swing.JButton pollButton = new javax.swing.JButton(Bundle.getMessage("LabelPoll"));
    javax.swing.JTextField uaAddrField = new javax.swing.JTextField(5);

    private OakTreeSystemConnectionMemo _memo = null;

    public SerialPacketGenFrame(OakTreeSystemConnectionMemo memo) {
        super();
        _memo = memo;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void initComponents() {
        // the following code sets the frame's initial state

        jLabel1.setText(Bundle.getMessage("CommandLabel"));
        jLabel1.setVisible(true);

        sendButton.setText(Bundle.getMessage("ButtonSend"));
        sendButton.setVisible(true);
        sendButton.setToolTipText(Bundle.getMessage("TooltipSendPacket"));

        packetTextField.setText("");
        packetTextField.setToolTipText(Bundle.getMessage("EnterHexToolTip"));
        packetTextField.setMaximumSize(
                new Dimension(packetTextField.getMaximumSize().width,
                        packetTextField.getPreferredSize().height
                )
        );

        setTitle(Bundle.getMessage("SendXCommandTitle", "OakTree"));
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        getContentPane().add(jLabel1);
        getContentPane().add(packetTextField);
        getContentPane().add(sendButton);

        sendButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                sendButtonActionPerformed(e);
            }
        });

        getContentPane().add(new JSeparator(JSeparator.HORIZONTAL));

        // add poll message buttons
        JPanel pane3 = new JPanel();
        pane3.setLayout(new FlowLayout());
        pane3.add(new JLabel(Bundle.getMessage("LabelNodeAddress")));
        pane3.add(uaAddrField);
        pane3.add(pollButton);
        pollButton.setToolTipText(Bundle.getMessage("PollToolTip"));
        uaAddrField.setText("0");
        uaAddrField.setToolTipText(Bundle.getMessage("TooltipNodeAddress"));
        getContentPane().add(pane3);

        pollButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                pollButtonActionPerformed(e);
            }
        });

        // pack for display
        pack();
    }

    public void pollButtonActionPerformed(java.awt.event.ActionEvent e) {
        SerialMessage msg = SerialMessage.getPoll(Integer.parseInt(uaAddrField.getText()));
        _memo.getTrafficController().sendSerialMessage(msg, this);
    }

    public void sendButtonActionPerformed(java.awt.event.ActionEvent e) {
        _memo.getTrafficController().sendSerialMessage(createPacket(packetTextField.getText()), this);
    }

    SerialMessage createPacket(String s) {
        // gather bytes in result
        byte b[] = StringUtil.bytesFromHexString(s);
        if ((b.length < 4) || (b.length > 5)) {
            return null;  // no such thing as message with other than 4 or 5 bytes
        }
        SerialMessage m = new SerialMessage(5);
        for (int i = 0; i < b.length; i++) {
            m.setElement(i, b[i]);
        }
        return m;
    }

    /** 
     * {@inheritDoc}
     * Ignores messages.
     */
    @Override
    public void message(SerialMessage m) {
    }

    /** 
     * {@inheritDoc}
     * Ignore replies.
     */
    @Override
    public void reply(SerialReply r) {
    }

}
