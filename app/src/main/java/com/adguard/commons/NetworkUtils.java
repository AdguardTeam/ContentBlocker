package com.adguard.commons;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.adguard.commons.concurrent.ExecutorsPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Helper methods for working with the network
 */
public class NetworkUtils {

    private final static Logger LOG = LoggerFactory.getLogger(NetworkUtils.class);

    /**
     * Learn more here:
     * http://stackoverflow.com/questions/3070144/how-do-you-get-the-current-dns-servers-for-android
     *
     * @return List of current DNS servers
     */
    public static List<String> getDnsServers() {
        try {
            Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
            //noinspection RedundantArrayCreation
            Method method = SystemProperties.getMethod("get", new Class[]{String.class});
            ArrayList<String> servers = new ArrayList<>();
            for (String name : new String[]{"net.dns1", "net.dns2", "net.dns3", "net.dns4",}) {
                String value = (String) method.invoke(null, name);
                if (value != null && !"".equals(value) && !servers.contains(value)) {
                    servers.add(value);
                }
            }

            return servers;
        } catch (Throwable ex) {
            LOG.warn("Cannot get DNS servers:\r\n", ex);
            return null;
        }
    }

    /**
     * Checks if network is available
     *
     * @param context Context
     * @return true if available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Returns current network connection info
     *
     * @param context Context
     * @return NetworkInfo about current connection, or null if there is no connection
     */
    public static NetworkInfo getCurrentConnection(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    /**
     * @param context Current context
     * @return current connection type
     */
    public static Integer getConnectionType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return networkInfo.getType();
        }

        return null;
    }

    /**
     * @param context Current context
     * @return is current connection wifi
     */
    public static boolean isConnectionWifi(Context context) {
        final Integer connectionType = NetworkUtils.getConnectionType(context);
        //noinspection Annotator
        return connectionType != null && (connectionType == ConnectivityManager.TYPE_WIFI || connectionType == ConnectivityManager.TYPE_ETHERNET);
    }

    /**
     * This method causes new packet to be written to TUN.
     * We need this to close VPN faster and don't wait until poll timeout is hit.
     */
    public static void pingNetwork() {

        ExecutorsPool.getCachedExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;

                try {
                    InetSocketAddress localInetSocketAddress = new InetSocketAddress("8.8.8.8", 53);
                    byte[] arrayOfByte = "".getBytes();

                    DatagramPacket localDatagramPacket = new DatagramPacket(arrayOfByte, arrayOfByte.length, localInetSocketAddress);
                    socket = new DatagramSocket();
                    socket.send(localDatagramPacket);
                } catch (Exception ex) {
                    LoggerFactory.getLogger(NetworkUtils.class).debug("Cannot ping network:\r\n", ex);
                } finally {
                    if (socket != null) {
                        socket.close();
                    }
                }
            }
        });
    }

    /**
     * Get existence of mobile network on this device
     *
     * @param context Current context
     * @return true if this device is able to use mobile network
     */
    public static boolean hasMobileNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null);
    }

    /**
     * Checks if network interface has IPv6 address
     *
     * @param networkInterface Network interface
     * @return true if network interface has IPv6 address
     */
    public static boolean hasIPv6Address(NetworkInterface networkInterface) throws SocketException {
        if (networkInterface == null || networkInterface.getInterfaceAddresses() == null) {
            return false;
        }

        for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
            InetAddress inetAddress = address.getAddress();

            if (inetAddress instanceof Inet6Address &&
                    !inetAddress.isLinkLocalAddress() &&
                    !inetAddress.isLoopbackAddress() &&
                    networkInterface.isUp()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get if any of the current network interfaces has IPv6 connectivity
     *
     * @return true if we have IPv6 network connectivity
     */
    public static boolean hasIPv6Network() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (!networkInterface.isLoopback()) {
                    printInterfaceData(networkInterface);
                }

                if (hasIPv6Address(networkInterface)) {
                    // Found IPv6 address
                    LOG.info("Found IPv6 address for network {}", networkInterface);
                    return true;
                }
            }
        } catch (SocketException e) {
            LOG.error("Error while checking for IPv6 network presence", e);
        }

        return false;
    }

    /**
     * Gets IPv4 InetAddress by host name
     *
     * @param host Host name
     * @return InetAddress
     */
    public static InetAddress getIp4Address(String host) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            if (addresses != null) {
                for (InetAddress address : addresses) {
                    if (address instanceof Inet4Address) {
                        return address;
                    }
                }
            }
        } catch (UnknownHostException e) {
            LOG.error("Cannot get IPv4 address for {}\n", host, e);
        }
        return null;
    }

    private static void printInterfaceData(NetworkInterface i) {
        StringBuilder sb = new StringBuilder();
        boolean up = false;
        try {
            up = i.isUp();
        } catch (SocketException e) {
            LOG.error("Error: {}", e);
        }
        sb.append(i.getDisplayName())
                .append(" (")
                .append(i.getName())
                .append(") ")
                .append(i.toString())
                .append(" isUp = ").append(up);
        LOG.info("Interface: {}", sb.toString());
    }
}
