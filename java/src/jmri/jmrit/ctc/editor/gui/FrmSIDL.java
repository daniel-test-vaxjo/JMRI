package jmri.jmrit.ctc.editor.gui;

import jmri.jmrit.ctc.editor.code.AwtWindowProperties;
import jmri.jmrit.ctc.editor.code.CheckJMRIObject;
import jmri.jmrit.ctc.editor.code.CodeButtonHandlerDataRoutines;
import jmri.jmrit.ctc.editor.code.CommonSubs;
import jmri.jmrit.ctc.editor.code.ProgramProperties;
import java.util.ArrayList;
import jmri.jmrit.ctc.ctcserialdata.CodeButtonHandlerData;
import jmri.jmrit.ctc.ctcserialdata.ProjectsCommonSubs;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 *
 * @author Gregory J. Bedlek Copyright (C) 2018, 2019
 */
@API(status = MAINTAINED)
public class FrmSIDL extends javax.swing.JFrame {

    /**
     * Creates new form DlgSIDL
     */
    private static final String FORM_PROPERTIES = "DlgSIDL";    // NOI18N
    private static final String PREFIX = "_mSIDL_";             // NOI18N
    private final AwtWindowProperties _mAwtWindowProperties;
    private boolean _mClosedNormally = false;
    public boolean closedNormally() { return _mClosedNormally; }
    private final CodeButtonHandlerData _mCodeButtonHandlerData;
    private final ProgramProperties _mProgramProperties;
    private final CheckJMRIObject _mCheckJMRIObject;

    private String _mSIDL_LeftInternalSensorOrig;
    private String _mSIDL_NormalInternalSensorOrig;
    private String _mSIDL_RightInternalSensorOrig;
    private void initOrig() {
        _mSIDL_LeftInternalSensorOrig = _mCodeButtonHandlerData._mSIDL_LeftInternalSensor;
        _mSIDL_NormalInternalSensorOrig = _mCodeButtonHandlerData._mSIDL_NormalInternalSensor;
        _mSIDL_RightInternalSensorOrig = _mCodeButtonHandlerData._mSIDL_RightInternalSensor;
    }
    private boolean dataChanged() {
        if (!_mSIDL_LeftInternalSensorOrig.equals(_mSIDL_LeftInternalSensor.getText())) return true;
        if (!_mSIDL_NormalInternalSensorOrig.equals(_mSIDL_NormalInternalSensor.getText())) return true;
        if (!_mSIDL_RightInternalSensorOrig.equals(_mSIDL_RightInternalSensor.getText())) return true;
        return false;
    }

    public FrmSIDL( AwtWindowProperties awtWindowProperties, CodeButtonHandlerData codeButtonHandlerData,
                    ProgramProperties programProperties, CheckJMRIObject checkJMRIObject) {
        super();
        initComponents();
        CommonSubs.addHelpMenu(this, "package.jmri.jmrit.ctc.CTC_frmSIDL", true);  // NOI18N
        _mAwtWindowProperties = awtWindowProperties;
        _mCodeButtonHandlerData = codeButtonHandlerData;
        _mProgramProperties = programProperties;
        _mCheckJMRIObject = checkJMRIObject;
        _mSIDL_LeftInternalSensor.setText(_mCodeButtonHandlerData._mSIDL_LeftInternalSensor);
        _mSIDL_NormalInternalSensor.setText(_mCodeButtonHandlerData._mSIDL_NormalInternalSensor);
        _mSIDL_RightInternalSensor.setText(_mCodeButtonHandlerData._mSIDL_RightInternalSensor);
        initOrig();
        _mAwtWindowProperties.setWindowState(this, FORM_PROPERTIES);
        this.getRootPane().setDefaultButton(_mSaveAndClose);
    }

    public static boolean dialogCodeButtonHandlerDataValid(CheckJMRIObject checkJMRIObject, CodeButtonHandlerData codeButtonHandlerData) {
        if (!codeButtonHandlerData._mSIDL_Enabled) return true; // Not enabled, can be no error!
//  For interrelationship(s) checks:
        boolean leftInternalSensorPresent = !ProjectsCommonSubs.isNullOrEmptyString(codeButtonHandlerData._mSIDL_LeftInternalSensor);
        boolean rightInternalSensorPresent = !ProjectsCommonSubs.isNullOrEmptyString(codeButtonHandlerData._mSIDL_RightInternalSensor);
//  Checks:
        if (ProjectsCommonSubs.isNullOrEmptyString(codeButtonHandlerData._mSIDL_NormalInternalSensor)) return false;
        if (!leftInternalSensorPresent && !rightInternalSensorPresent) return false;
        return checkJMRIObject.validClassWithPrefix(PREFIX, codeButtonHandlerData);
    }

//  Validate all internal fields as much as possible:
    private ArrayList<String> formFieldsValid() {
        ArrayList<String> errors = new ArrayList<>();
//  For interrelationship(s) checks:
        boolean leftInternalSensorPresent = CommonSubs.isJTextFieldNotEmpty(_mSIDL_LeftInternalSensor);
        boolean rightInternalSensorPresent = CommonSubs.isJTextFieldNotEmpty(_mSIDL_RightInternalSensor);
//  Checks:
        CommonSubs.checkJTextFieldNotEmpty(_mSIDL_NormalInternalSensor, _mSIDL_NormalInternalSensorPrompt, errors);
        if (!leftInternalSensorPresent && !rightInternalSensorPresent) errors.add(Bundle.getMessage("OneOrBothOf") + " \"" + _mSIDL_LeftInternalSensorPrompt.getText() + "\" " + Bundle.getMessage("And") + " \"" + _mSIDL_RightInternalSensorPrompt.getText() + "\" " + Bundle.getMessage("MustBePresent"));   // NOI18N
        _mCheckJMRIObject.analyzeForm(PREFIX, this, errors);
        return errors;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        _mSaveAndClose = new javax.swing.JButton();
        _mSIDL_LeftInternalSensorPrompt = new javax.swing.JLabel();
        _mSIDL_LeftInternalSensor = new javax.swing.JTextField();
        _mSIDL_NormalInternalSensorPrompt = new javax.swing.JLabel();
        _mSIDL_NormalInternalSensor = new javax.swing.JTextField();
        _mSIDL_RightInternalSensorPrompt = new javax.swing.JLabel();
        _mSIDL_RightInternalSensor = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getMessage("TitleDlgSIDL"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        _mSaveAndClose.setText(Bundle.getMessage("ButtonSaveClose"));
        _mSaveAndClose.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mSaveAndCloseActionPerformed(evt);
            }
        });

        _mSIDL_LeftInternalSensorPrompt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        _mSIDL_LeftInternalSensorPrompt.setText(Bundle.getMessage("LabelDlgSIDLLeft"));

        _mSIDL_NormalInternalSensorPrompt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        _mSIDL_NormalInternalSensorPrompt.setText(Bundle.getMessage("LabelDlgSIDLNormal"));

        _mSIDL_RightInternalSensorPrompt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        _mSIDL_RightInternalSensorPrompt.setText(Bundle.getMessage("LabelDlgSIDLRight"));

        jButton2.setText(Bundle.getMessage("ButtonReapply"));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(_mSIDL_RightInternalSensorPrompt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                            .addComponent(_mSIDL_LeftInternalSensorPrompt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(_mSIDL_NormalInternalSensorPrompt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(_mSIDL_RightInternalSensor, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(_mSIDL_NormalInternalSensor, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(_mSIDL_LeftInternalSensor, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(63, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addGap(28, 28, 28))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(_mSaveAndClose)
                .addGap(72, 72, 72))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mSIDL_LeftInternalSensorPrompt)
                    .addComponent(_mSIDL_LeftInternalSensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mSIDL_NormalInternalSensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mSIDL_NormalInternalSensorPrompt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mSIDL_RightInternalSensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mSIDL_RightInternalSensorPrompt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(_mSaveAndClose)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void _mSaveAndCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mSaveAndCloseActionPerformed
        if (CommonSubs.missingFieldsErrorDialogDisplayed(this, formFieldsValid(), false)) {
            return; // Do not allow exit or transfer of data.
        }
        _mClosedNormally = true;
        _mCodeButtonHandlerData._mSIDL_LeftInternalSensor = _mSIDL_LeftInternalSensor.getText();
        _mCodeButtonHandlerData._mSIDL_NormalInternalSensor = _mSIDL_NormalInternalSensor.getText();
        _mCodeButtonHandlerData._mSIDL_RightInternalSensor = _mSIDL_RightInternalSensor.getText();
        _mAwtWindowProperties.saveWindowState(this, FORM_PROPERTIES);
        dispose();
    }//GEN-LAST:event__mSaveAndCloseActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        _mAwtWindowProperties.saveWindowState(this, FORM_PROPERTIES);
        if (CommonSubs.allowClose(this, dataChanged())) dispose();
    }//GEN-LAST:event_formWindowClosing

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        CodeButtonHandlerData temp = _mCodeButtonHandlerData.deepCopy();
        temp = CodeButtonHandlerDataRoutines.uECBHDWSD_SIDL(_mProgramProperties, temp);
        _mSIDL_LeftInternalSensor.setText(temp._mSIDL_LeftInternalSensor);
        _mSIDL_NormalInternalSensor.setText(temp._mSIDL_NormalInternalSensor);
        _mSIDL_RightInternalSensor.setText(temp._mSIDL_RightInternalSensor);
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField _mSIDL_LeftInternalSensor;
    private javax.swing.JLabel _mSIDL_LeftInternalSensorPrompt;
    private javax.swing.JTextField _mSIDL_NormalInternalSensor;
    private javax.swing.JLabel _mSIDL_NormalInternalSensorPrompt;
    private javax.swing.JTextField _mSIDL_RightInternalSensor;
    private javax.swing.JLabel _mSIDL_RightInternalSensorPrompt;
    private javax.swing.JButton _mSaveAndClose;
    private javax.swing.JButton jButton2;
    // End of variables declaration//GEN-END:variables
}
