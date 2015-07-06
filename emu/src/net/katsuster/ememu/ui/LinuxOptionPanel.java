package net.katsuster.ememu.ui;

import java.net.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Linux 起動時に渡すオプションを設定するパネル。
 *
 * @author katsuhiro
 */
public class LinuxOptionPanel extends JPanel {
    private LinuxOption opts;

    private JTextField txtImage, txtInitram, txtCmdline;

    /**
     * Linux 起動時に渡すオプションを設定するパネルを作成します。
     */
    public LinuxOptionPanel() {
        this(new LinuxOption());
    }

    /**
     * Linux 起動時に渡すオプションを設定するパネルを作成します。
     *
     * パネル内の各コンポーネントには指定したデフォルト値が設定されます。
     *
     * @param options オプションのデフォルト値
     */
    public LinuxOptionPanel(LinuxOption options) {
        super(true);

        opts = options;

        //Get prefered size(40 chars)
        JTextField tmpTxt = new JTextField(String.format("%040x", 0));
        Dimension dim = tmpTxt.getPreferredSize();

        txtImage = new JTextField(opts.getKernelImage().toString());
        txtImage.setPreferredSize(dim);
        txtImage.setMinimumSize(dim);
        txtInitram = new JTextField(opts.getInitramfsImage().toString());
        txtInitram.setPreferredSize(dim);
        txtInitram.setMinimumSize(dim);
        txtCmdline = new JTextField(opts.getCommandLine());
        txtCmdline.setPreferredSize(dim);
        txtCmdline.setMinimumSize(dim);

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        GridBagLayoutHelper.add(this, layout, new JLabel("Kernel Image", SwingConstants.RIGHT),
                0, 0, 1, 1);
        GridBagLayoutHelper.add(this, layout, txtImage,
                1, 0, GridBagConstraints.RELATIVE, 1);
        GridBagLayoutHelper.add(this, layout, new JLabel("InitramFS Image", SwingConstants.RIGHT),
                0, 1, 1, 1);
        GridBagLayoutHelper.add(this, layout, txtInitram,
                1, 1, GridBagConstraints.RELATIVE, 1);
        GridBagLayoutHelper.add(this, layout, new JLabel("Command line", SwingConstants.RIGHT),
                0, 2, 1, 1);
        GridBagLayoutHelper.add(this, layout, txtCmdline,
                1, 2, GridBagConstraints.RELATIVE, 1);

        setBorder(BorderFactory.createTitledBorder("Linux Boot Options"));
    }

    /**
     * Linux 起動時に渡すオプションを取得します。
     *
     * @return エミュレータで Linux 起動時に渡すオプション
     */
    public LinuxOption getOption() {
        updateOption();

        return opts;
    }

    /**
     * パネル内の各コンポーネントの設定値をオプションオブジェクトに反映させます。
     */
    protected void updateOption() {
        try {
            opts.setKernelImage(new URI(txtImage.getText()));
            opts.setInitramfsImage(new URI(txtInitram.getText()));
            opts.setCommandLine(txtCmdline.getText());
        } catch (URISyntaxException e) {
            //ignored
        }
    }
}
