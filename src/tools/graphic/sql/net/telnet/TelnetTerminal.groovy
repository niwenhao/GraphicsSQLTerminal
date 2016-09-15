package tools.graphic.sql.net.telnet

import org.apache.commons.net.telnet.TelnetClient
import tools.graphic.sql.config.Log
import tools.graphic.sql.net.Terminal

/**
 * Terminal�C���^�t�F�[�X��Telnet�Ή�����
 *
 * Created by nwh on 2015/12/29.
 */
class TelnetTerminal extends TelnetClient implements Terminal {
    /***
     * �ڑ����
     */
    enum Status { CONECTED, WAITING, TERMINAZED }
    Status status

    /***
     * ������ɂ���R�[�f�B���O
     */
    String encoding = System.properties["file.encoding"]

    /***
     * �o�b�t�@
     */
    private byte[] buf = new byte[8192]

    /***
     * Telnet�ڑ��쐬
     *
     * @param term      �[����ށiVT100�Ȃǁj
     * @param host      �z�X�g��
     * @param port      �|�[�g
     */
    public TelnetTerminal(String term, String host, int port) {
        super(term)
        try {
            connect(host, port)
            status = Status.CONECTED
        } catch(e) {
            status = Status.TERMINAZED
            throw new RuntimeException(e)
        }
    }

    /***
     * �p�X���[�h�𑗐M����B
     *
     * @param pwd   �p�X���[�h�e�L�X�g
     * @param mask  �\�������e�L�X�g
     */
    @Override
    void password(String pwd, String mask) {
        Log.logConsole.red("${mask}\n")
        this.outputStream << pwd << "\n"
        this.outputStream.flush()
        sleep(100)
    }

    /**
     * ���f�[�^���M
     *
     * @param data ���M�f�[�^
     */
    void write(byte[] data) {
        Log.logConsole.red("${data}\n")
        this.outputStream.write(data)
        this.outputStream.flush()
    }

    /**
     * �e�L�X�g���M
     *
     * @param message   ���M�e�L�X�g
     */
    void puts(String message) {
        Log.logConsole.red("${message}\n")
        synchronized (buf) {
            this.outputStream << message << "\n"
            this.outputStream.flush()
            sleep(100)
        }
    }

    /***
     * �e�L�X�g��M
     *
     * @return  ��M�e�L�X�g�i�����s�A�s���S�s�����肤��j
     */
    String gets() {
        Log.debug("gets")
        int len

        synchronized (buf) {
            status = Status.WAITING
            try {
                while((len = this.inputStream.read(buf)) <= 0) sleep 100
                String r = new String(buf, 0, len, encoding)
                Log.logConsole.blue("${r}")
                return r
            } finally {
                status = Status.CONECTED
            }
        }
    }

}
