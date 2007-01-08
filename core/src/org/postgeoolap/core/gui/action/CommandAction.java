package org.postgeoolap.core.gui.action;

import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/**
 * CommandAction &eacute; a classe respons&aacute;vel por armazenar informa&ccedil;&otilde;es referentes 
 * a um commando espec&iacute;fico (ver padr&atilde;o de projeto Command, da GoF), permitindo, 
 * al&eacute;m disto, especificar caracter&iacute;sticas de representa&ccedil;&atilde;o visual, como 
 * &iacute;cones, mnem&ocirc;nicos, aceleradores e tooltips. 
 * 
 * @version 1.0 RC1
 * @since 1.0
 */
public abstract class CommandAction extends AbstractAction
{
     
    /**
     * Cria um CommandAction especificando nome, &iacute;cone, acelerador, mnem&ocirc;nico e tooltip.
     * 
     * @param name nome da a&ccedil;&atilde;o
     * @param icon &iacute;cone referente &agrave; a&ccedil;&atilde;o
     * @param keyStroke acelerador que aciona a a&ccedil;&atilde;o
     * @param mnemonic mnem&ocirc;nico do texto (name) que acompanha a a&ccedil;&atilde;o
     * @param toolTip dica a ser apresentada sobre a a&ccedil;&atilde;o
     */
	public CommandAction(String name, URL icon, KeyStroke keyStroke,
		String mnemonic, String toolTip)
	{
        if (name != null)
            this.putValue(Action.NAME, name);
		if (icon != null)
			this.putValue(Action.SMALL_ICON, new ImageIcon(icon));
        if (keyStroke != null)
            this.putValue(Action.ACCELERATOR_KEY, keyStroke);
        if (mnemonic != null)
            this.putValue(Action.MNEMONIC_KEY, mnemonic);
        if (toolTip != null)
            this.putValue(Action.SHORT_DESCRIPTION, toolTip);
	}
}