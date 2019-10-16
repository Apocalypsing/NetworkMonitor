package net.apocalypsing.networkmonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkMonitor {
    private static Timer _mainTimer = new Timer();
    private static Boolean _usingSockets = true;
    private static Boolean _gatewayStatus = false;
    private static Boolean _verboseStats = false;
    private static String _gatewayAddress = "203.219.198.72";
    private static int _totalSamplesSent = 0;
    private static int _totalSamplesReceived = 0;
    private static int _totalSamplesLost = 0;
    private static int _totalDropoutCount = 0;
    private static Short _dropoutThreshold = 25;
    private static Short _timeoutThreshold = 500;
    private static Short _samplesLost = 0;
    private static Short _sampleRate = 2000;

    public static void main(String[] args) {
        printParameters();
        _mainTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                monitorGateway();
                printStats();
            }
        }, 0, _sampleRate);
    }

    public static void printStats() {
        if (_verboseStats == true) {
            System.out.println("Sent: " + _totalSamplesSent + " - received: " + _totalSamplesReceived
                    + " - current losses: " + _samplesLost + " | total losses: " + _totalSamplesLost + " - dropouts: "
                    + _totalDropoutCount);
        } else {
            System.out.println("Sent: " + _totalSamplesSent + " - received: " + _totalSamplesReceived + " | losses: "
                    + _totalSamplesLost + " - dropouts: " + _totalDropoutCount);
        }
    }

    public static void printParameters() {
        System.out.println("Verbose stats enabled: " + _verboseStats);
        System.out.println("Using sockets: " + _usingSockets);
        System.out.println("Gateway address: " + _gatewayAddress);
        System.out.println("Timeout threshold: " + _timeoutThreshold + "ms");
        System.out.println("Dropout threshold: " + _dropoutThreshold + " timeouts");
        System.out.println("Sample rate: once every " + _sampleRate + "ms\n");
    }

    public static void monitorGateway() {
        _totalSamplesSent++;
        try {
            InetAddress gatewayAddress = InetAddress.getByName(_gatewayAddress);
            _gatewayStatus = gatewayAddress.isReachable(_timeoutThreshold);
            if (_gatewayStatus == true) {
                _samplesLost = 0;
                _totalSamplesReceived++;
            } else {
                _samplesLost++;
                _totalSamplesLost++;
                if (_samplesLost == _dropoutThreshold) {
                    _totalDropoutCount++;
                }
            }
        } catch (Exception ex) {
            _samplesLost++;
            _totalSamplesLost++;
            if (_samplesLost == _dropoutThreshold) {
                _totalDropoutCount++;
            }
        }
    }
}