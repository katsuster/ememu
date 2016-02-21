package net.katsuster.ememu.ui;

import java.net.*;
import java.awt.*;
import javax.swing.*;

/**
 * プロキシオプションを設定するパネル。
 *
 * @author katsuhiro
 */
public class ProxyOptionPanel extends JPanel {
    private ProxyOption opts;

    private JCheckBox chkSet;
    private JTextField txtHost, txtPort;

    /**
     * プロキシオプションを設定するパネルを作成します。
     */
    public ProxyOptionPanel() {
        this(new ProxyOption());
    }

    /**
     * プロキシオプションを設定するパネルを作成します。
     *
     * パネル内の各コンポーネントには指定したデフォルト値が設定されます。
     *
     * @param options オプションのデフォルト値
     */
    public ProxyOptionPanel(ProxyOption options) {
        super(true);

        opts = options;

        //コンポーネントを作成
        chkSet = new JCheckBox("Enable proxy configuration");
        if (opts.getProxyHost().toString().equals("")) {
            chkSet.setSelected(false);
        } else {
            chkSet.setSelected(true);
        }

        txtHost = new JTextField(opts.getProxyHost().toString());
        txtHost.setPreferredSize(calcPreferredSize(30));
        txtHost.setMinimumSize(calcPreferredSize(30));

        txtPort = new JTextField(Integer.toString(opts.getProxyPort()));
        txtPort.setPreferredSize(calcPreferredSize(10));
        txtPort.setMinimumSize(calcPreferredSize(10));

        //コンポーネントを配置
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        GridBagLayoutHelper.add(this, layout, chkSet,
                0, 0, 2, 1);

        GridBagLayoutHelper.add(this, layout, new JLabel("Host:", SwingConstants.RIGHT),
                0, 1, 1, 1);
        GridBagLayoutHelper.add(this, layout, txtHost,
                1, 1, 1, 1);

        GridBagLayoutHelper.add(this, layout, new JLabel("Port:", SwingConstants.RIGHT),
                0, 2, 1, 1);
        GridBagLayoutHelper.add(this, layout, txtPort,
                1, 2, 1, 1);

        setBorder(BorderFactory.createTitledBorder("Proxies"));
    }

    /**
     * プロキシオプションを取得します。
     *
     * @return プロキシオプション
     */
    public ProxyOption getOption() {
        updateOption();

        return opts;
    }

    /**
     * パネル内の各コンポーネントの設定値をオプションオブジェクトに反映させます。
     *
     * 設定値が URI として（ホスト名の場合）解釈できない、
     * または数値として（ポート番号の場合）解釈できない場合は、
     * オプションオブジェクトの値は変更されません。
     */
    protected void updateOption() {
        try {
            if (chkSet.isSelected()) {
                opts.setProxyHost(new URI(txtHost.getText()));
                opts.setProxyPort(Integer.valueOf(txtPort.getText()));
            } else {
                opts.setProxyHost(new URI(""));
                opts.setProxyPort(0);
            }
        } catch (URISyntaxException | NumberFormatException e) {
            //ignored
        }
        txtHost.setText(opts.getProxyHost().toString());
        txtPort.setText(Integer.toString(opts.getProxyPort()));
    }

    /**
     * "0" を指定された数だけ並べたときのコンポーネントの推奨サイズを取得します。
     *
     * @param chars 文字を並べる個数
     * @return コンポーネントのサイズ
     */
    protected Dimension calcPreferredSize(int chars) {
        JTextField tmpTxt = new JTextField(
                String.format("%0" + Integer.toString(chars) + "x", 0));

        return tmpTxt.getPreferredSize();
    }
}
