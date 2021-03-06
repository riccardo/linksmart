package eu.linksmart.event.mqtt.impl;

import com.google.gson.Gson;
import eu.linksmart.api.event.EventPublicationWrapper;
import eu.linksmart.api.event.EventSubscriber;
import eu.linksmart.api.event.EventSubscriptionWrapper;
import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;
import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.osgi.service.component.ComponentContext;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;

@Component(name="MqttServiceProvider", immediate=true)
@Service({EventPublicationWrapper.class})
public class MqttServiceProvider implements EventPublicationWrapper,EventSubscriptionWrapper {

    private Logger mLogger = Logger.getLogger(MqttServiceProvider.class.getName());
    protected ComponentContext mContext;



    private Map<String,MqttClient> clients;

    private Gson parser=null;
    protected boolean init() {
        System.out.println("hola");
        clients = new Hashtable<String,MqttClient>();
        parser = new Gson();

        // TODO Auto-generated method stub
        return true;
    }
    private void shutdown(){
        if (!clients.isEmpty()){

            try {
                for(MqttClient client : clients.values())
                    client.disconnect();

                clients.clear();
            } catch (MqttException e) {
              mLogger.error(e.getStackTrace());

            }

        }
    }

    @Override
    public void findEventManager(String clientId, String brokerPID) {

        // Create a new connection
        if(!clients.containsKey(clientId)){

            if (brokerPID == "" || brokerPID == null)
                brokerPID = "tcp://localhost:1883";
            clients.put(clientId, connect(clientId, brokerPID) );

        }
        // the connection exist but is broken
        else if (!clients.get(clientId).isConnected()){

            clients.put(clientId, connect(clientId, brokerPID) );
        }

    }

    private MqttClient connect(String clientId, String brokerPID){

        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient client = null;
        try {
            client = new MqttClient(brokerPID, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.connect(connOpts);


        } catch(MqttException e) {
            mLogger.error(e.getStackTrace());
        }

        return client;
    }
    @Override
    public boolean publishEvent(String serviceID, String topic,
                                Part[] valueParts) throws RemoteException {
        try {

            if (clients.get(serviceID).isConnected()){
                MqttMessage message = new MqttMessage(new Gson().toJson(valueParts).getBytes());
                message.setQos(2);
                clients.get(serviceID).publish(topic, message);
            }else
                throw new RemoteException("Error the conection with ID "+serviceID+ " is broken");


        } catch(MqttException me) {
            throw new RemoteException("MQTT Error No. "+me.getReasonCode()+" located "+me.getLocalizedMessage()+':'+me.getMessage(), me.getCause() );
        }
        return true;
    }


    @Override
    public boolean isEventManagerLocated(String serviceID) {
        return clients.get(serviceID).isConnected();
    }
    @Override
    public void registerCallback(EventSubscriber subscriber,
                                 String serviceID) {

        clients.get(serviceID).setCallback(new CallBackerImpl(subscriber));

    }
    @Override
    public void deregisterCallback(String serviceID) {
        try {
            if(clients != null)
                if(clients.containsKey(serviceID))
                    clients.get(serviceID).disconnect();
        } catch (MqttException e) {
            mLogger.error(e.getStackTrace());
        }
    }
    @Override
    public void subscribeWithTopic(String serviceID, String topic) {
        try {
            clients.get(serviceID).subscribe(topic, 2);

        } catch (MqttException e) {

            mLogger.error(e.getStackTrace());
        }

    }
    @Override
    public void unsubscribeTopic(String serviceID, String topic) {
        try {
            clients.get(serviceID).unsubscribe(topic);
        } catch (MqttException e) {

            mLogger.error(e.getStackTrace());
        }

    }
    @Override
    public void unsubscribeAllTopics(String serviceID) {
        try {
            clients.get("").close();
            clients.get(serviceID).unsubscribe("*");
        } catch (MqttException e) {
            mLogger.error(e.getStackTrace());

        }
    }
    /// =======================================================================================================================
    private static final String CXF_SERVICES_PATH = "http://localhost:9090/cxf/services/";


    protected ComponentContext context;
    @Reference(name="NetworkManager",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindNet",
            unbind="unbindNet",
            policy= ReferencePolicy.DYNAMIC)
    private NetworkManager networkManager;
    public final String SERVICE_ID = this.getClass().getSimpleName();
    protected String backbone;
    protected VirtualAddress myVirtualAddressPublicator;
    protected VirtualAddress myVirtualAddressSubscriber;

    @Activate
    protected void activate(ComponentContext context) throws Exception {
        this.context = context;

        if (!init())
            throw new Exception("Error: The "+this.getClass().getCanonicalName()+" init was not able to execute successfully!");

        registerService();

        mLogger.info(this.getClass()+" activated");
    }
    @Deactivate
    protected void deactivate(ComponentContext context) throws Exception {
        shutdown();
        mLogger.info(this.getClass()+" deactivated");
    }

    private VirtualAddress createService(String endpoint, String serviceDescription) {
        try {
            Registration registration = networkManager.registerService(
                    new eu.linksmart.utils.Part[] { new eu.linksmart.utils.Part(ServiceAttribute.DESCRIPTION.name(),
                            serviceDescription) }, endpoint, backbone);
            return registration.getVirtualAddress();
        } catch (RemoteException e) {
            mLogger.error(e.getStackTrace());
            return null;
        }
    }
    private void initBackbone() {
        try {
            String[] backbones = networkManager.getAvailableBackbones();
            this.backbone = null;
            for (String b : backbones) {
                if (b.contains("soap")) {
                    this.backbone = b;
                }
            }
            if (backbone == null) {
                // Your web service will most likely only use "BackboneSOAPImpl"
                backbone = "eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl";
            }
        } catch (RemoteException e) {
            mLogger.error(e.getStackTrace());
        }
    }
    private void registerService(){

        String subscriberURL = CXF_SERVICES_PATH + EventSubscriptionWrapper.class.getSimpleName();

        myVirtualAddressSubscriber = createService(subscriberURL, EventSubscriptionWrapper.class.getSimpleName());


        // Publish as Web Service
        Hashtable props = new Hashtable();
       props = new Hashtable();
        props.put("service.exported.interfaces", "*");
        props.put("service.exported.configs", "org.apache.cxf.ws");
        props.put("org.apache.cxf.ws.address", subscriberURL);
        context.getBundleContext().registerService(	EventSubscriptionWrapper.class.getName(), this, props);
        //subscriberHIDs.put(DataFusionCore.class.getName(), virtualAddress);

    }

    protected synchronized void bindNet(NetworkManager nm) {
        networkManager =nm;

        initBackbone();


    }
    protected synchronized void unbindNet(NetworkManager nm) {
        try {
            networkManager.removeService(myVirtualAddressPublicator);

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            mLogger.error(e.getStackTrace());
        }
        //TODO: cuando se va el core todo debe morir!
    }
    /// =======================================================================================================================

    private class CallBackerImpl implements MqttCallback {
        EventSubscriber subscriber =null;
        CallBackerImpl(EventSubscriber subscriber){
            this.subscriber = subscriber;
        }

        @Override
        public void connectionLost(Throwable cause) {
            // TODO Auto-generated method stub

        }

        @Override
        public void messageArrived(String topic, MqttMessage message)
                throws Exception {


            String msg = new String(message.getPayload(),"UTF-8");
            Part [] partload= null;


            try {
                 partload = parser.fromJson(msg,Part[].class);
            }catch (Exception e){
                mLogger.info("Event is not a part array!");
            }
            subscriber.notify(topic,  partload);

            subscriber.notifyXmlEvent(topic, msg);

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

            // TODO Auto-generated method stub

        }

    }
}
