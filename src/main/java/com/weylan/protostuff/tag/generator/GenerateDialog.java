package com.weylan.protostuff.tag.generator;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;

public class GenerateDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox cbxTagStaticFields;
    private JCheckBox cbxTagTransientField;
    private JCheckBox cbxOnlyDel;
    private JTextField tagStartValue;

    public GenerateDialog() {
        setTitle("Protostuff Tag Generator");
        setContentPane(contentPane);
        setModal(true);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


    public boolean isDelOnlyDelete() {
        return cbxOnlyDel.isSelected();
    }

    public int getStaringNum() {
        String trim = tagStartValue.getText().trim();
        String num = StringUtils.isBlank(trim) ? "1" : trim;
        return Integer.parseInt(num);
    }

    public boolean isTagStaticFields() {
        return cbxTagStaticFields.isSelected();
    }

    public boolean isTagTransientFields() {
        return cbxTagTransientField.isSelected();
    }


}
