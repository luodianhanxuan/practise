package com.wangjg.kubernetes;

import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.*;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.extended.controller.Controller;
import io.kubernetes.client.extended.controller.ControllerManager;
import io.kubernetes.client.extended.controller.LeaderElectingController;
import io.kubernetes.client.extended.controller.builder.ControllerBuilder;
import io.kubernetes.client.extended.controller.reconciler.Reconciler;
import io.kubernetes.client.extended.controller.reconciler.Request;
import io.kubernetes.client.extended.controller.reconciler.Result;
import io.kubernetes.client.extended.event.EventType;
import io.kubernetes.client.extended.event.legacy.EventBroadcaster;
import io.kubernetes.client.extended.event.legacy.EventRecorder;
import io.kubernetes.client.extended.event.legacy.LegacyEventBroadcaster;
import io.kubernetes.client.extended.leaderelection.LeaderElectionConfig;
import io.kubernetes.client.extended.leaderelection.LeaderElector;
import io.kubernetes.client.extended.leaderelection.resourcelock.EndpointsLock;
import io.kubernetes.client.extended.pager.Pager;
import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.informer.cache.Lister;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.proto.Meta;
import io.kubernetes.client.proto.V1;
import io.kubernetes.client.util.*;
import io.kubernetes.client.util.exception.CopyNotSupportedException;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesApi;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * examples: https://github.com/kubernetes-client/java/wiki/3.-Code-Examples
 * restapi: https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.23/#-strong-api-overview-strong-
 */
@SuppressWarnings({"NewClassNamingConvention", "ConstantConditions"})
public class KubeClientExample {

    /*

        <!--   kubenetes client     -->
        <dependency>
            <groupId>io.kubernetes</groupId>
            <artifactId>client-java</artifactId>
            <version>14.0.0</version>
        </dependency>

        <dependency>
            <groupId>io.kubernetes</groupId>
            <artifactId>client-java-extended</artifactId>
            <version>14.0.0</version>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.9.0</version>
        </dependency>
        <dependency>
            <groupId>commons-cli</groupId>
            <artifactId>commons-cli</artifactId>
            <version>1.5.0</version>
        </dependency>

     */

    /*
        ################################   Configuration   #######################################
     */

    static String env = ObjectUtils.isEmpty(System.getenv("env")) ? "local" : System.getenv("env");


    private ApiClient getAndSetDefaultClient() throws IOException {
        ApiClient client;
        if (Objects.equals(env, "local")) {
            // file path to KubeConfig
            String kubeConfigPath = System.getenv("HOME") + "/.kube/quantex-paas.yaml";
            // loading the out-of-cluster config, a kubeconfig from file-system
            client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        } else {
            client = Config.defaultClient();
        }
        // set the global default api-client  to the in-cluster one from above
        Configuration.setDefaultApiClient(client);
        return client;
    }

    @Test
    public void outOfKubeCluster() throws IOException, ApiException {
        getAndSetDefaultClient();
        // the CoreV1Api loads default api-client from global configuration.
        CoreV1Api api = new CoreV1Api();
        // invokes the CoreV1Api client
        V1PodList list;
        list = api.listPodForAllNamespaces(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        for (V1Pod item : list.getItems()) {
            System.out.println(Objects.requireNonNull(item.getMetadata()).getName());
        }
    }

    @Test
    public void inKubeCluster() throws IOException, ApiException {
        ApiClient client = getAndSetDefaultClient();
        CoreV1Api api = new CoreV1Api();
        Watch<V1Namespace> watch = Watch.createWatch(
                client,
                api.listNamespaceCall(null,
                        null,
                        null,
                        null,
                        null,
                        5,
                        null,
                        null,
                        null,
                        Boolean.TRUE,
                        null),
                new TypeToken<Watch.Response<V1Namespace>>() {
                }.getType());

        for (Watch.Response<V1Namespace> item : watch) {
            System.out.printf("%s : %s%n", item.type, Objects.requireNonNull(item.object.getMetadata()).getName());
        }
    }

    /*
        ################################   Basics   #######################################
     */

    /**
     * Request/receive payloads in protobuf serialization protocol
     */
    @Test
    public void proto() throws IOException, ApiException {
        ApiClient client = getAndSetDefaultClient();

        ProtoClient pc = new ProtoClient(client);
        ProtoClient.ObjectOrStatus<V1.PodList> list = pc.list(V1.PodList.newBuilder(),
                "/api/v1/namespaces/turing-dev/pods");

        if (list.object.getItemsCount() > 0) {
            V1.Pod p = list.object.getItems(0);
            System.out.println(p);
        }

        V1.Namespace namespace = V1.Namespace.newBuilder().setMetadata(Meta.ObjectMeta.newBuilder().setName("test").build()).build();

        ProtoClient.ObjectOrStatus<V1.Namespace> ns = pc.create(namespace, "/api/v1/namespaces", "v1", "Namespace");
        System.out.println(ns);
        if (ns.object != null) {
            ns.object
                    .toBuilder()
                    .setSpec(V1.NamespaceSpec.newBuilder().addFinalizers("test").build())
                    .build();

            // this is how you would update an object, but you can't actually update namespace,
            // so this returns a 405
        }
        ns = pc.delete(V1.Namespace.newBuilder(), "/api/v1/namespaces/test");
        System.out.println(ns);
    }


    /**
     * Subscribe watch events from certain resources, equal to kubectl get <resource> -w
     */
    @Test
    public void watch() throws IOException, ApiException {
        ApiClient client = getAndSetDefaultClient();

        // infinite timeout
        OkHttpClient httpClient = client.getHttpClient()
                .newBuilder()
                .readTimeout(0, TimeUnit.SECONDS)
                .build();

        client.setHttpClient(httpClient);

        CoreV1Api api = new CoreV1Api();

        try (Watch<V1Namespace> watch = Watch.createWatch(
                client,
                api.listNamespaceCall(null,
                        null,
                        null,
                        null,
                        null,
                        5,
                        null,
                        null,
                        null,
                        true,
                        null),
                new TypeToken<Watch.Response<V1Namespace>>() {
                }.getType()
        )) {
            for (Watch.Response<V1Namespace> item : watch) {
                System.out.printf("%s: %s%n", item.type, Objects.requireNonNull(item.object.getMetadata()).getName());
            }
        }
    }

    /**
     * Fetch logs from running containers, equal to kubectl logs
     */
    @Test
    public void logs() throws IOException, ApiException {
        ApiClient client = getAndSetDefaultClient();

        CoreV1Api api = new CoreV1Api(client);

        PodLogs podLogs = new PodLogs();
        V1Pod pod =
                api.listNamespacedPod("default",
                                "false",
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null)
                        .getItems()
                        .get(0);

        InputStream is = podLogs.streamNamespacedPodLog(pod);
        Streams.copy(is, System.out);
    }


    /**
     * Establish an "exec" session with running containers, equal to kubectl exec
     */
    static class ExecExample {
        /**
         * A simple example of how to use the Java API
         *
         * <p>Easiest way to run this: mvn exec:java
         * -Dexec.mainClass="io.kubernetes.client.examples.ExecExample"
         *
         * <p>From inside $REPO_DIR/examples
         */
        public static void main(String[] args)
                throws IOException, ApiException, InterruptedException, ParseException {
            final Options options = new Options();
            options.addOption(new Option("p", "pod", true, "The name of the pod"));
            options.addOption(new Option("n", "namespace", true, "The namespace of the pod"));

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            String podName = cmd.getOptionValue("p", "nginx-dbddb74b8-s4cx5");
            String namespace = cmd.getOptionValue("n", "default");
            args = cmd.getArgs();

            ApiClient client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);

            Exec exec = new Exec();
            boolean tty = System.console() != null;
            // final Process proc = exec.exec("default", "nginx-4217019353-k5sn9", new String[]
            //   {"sh", "-c", "echo foo"}, true, tty);
            final Process proc =
                    exec.exec(namespace, podName, args.length == 0 ? new String[]{"sh"} : args, true, tty);

            Thread in =
                    new Thread(
                            () -> {
                                try {
                                    Streams.copy(System.in, proc.getOutputStream());
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
            in.start();

            Thread out =
                    new Thread(
                            () -> {
                                try {
                                    Streams.copy(proc.getInputStream(), System.out);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
            out.start();

            proc.waitFor();

            // wait for any last output; no need to wait for input thread
            out.join();

            proc.destroy();

            System.exit(proc.exitValue());
        }
    }


    /**
     * Maps local port to a port on the pod, equal to kubectl port-forward
     */
    @Test
    public void portForward() throws IOException, ApiException, InterruptedException {
        getAndSetDefaultClient();

        PortForward forward = new PortForward();
        List<Integer> ports = new ArrayList<>();
        int localPort = 8080;
        int targetPort = 8080;
        ports.add(targetPort);

        PortForward.PortForwardResult result =
                forward
                        .forward("default", "camera-viz-7949dbf7c6-lpxkd", ports);
        System.out.println("Forwarding!");
        ServerSocket ss = new ServerSocket(localPort);
        final Socket s = ss.accept();
        System.out.println("Connected!");
        new Thread(
                () -> {
                    try {
                        Streams.copy(result.getInputStream(targetPort), s.getOutputStream());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                })
                .start();

        new Thread(
                () -> {
                    try {
                        Streams.copy(s.getInputStream(), result.getOutboundStream(targetPort));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                })
                .start();

        Thread.sleep(10 * 1000);
        System.exit(0);
    }

    /**
     * Attach to a process that is already running inside an existing container, equal to kubectl attach
     */
    @Test
    public void attach() throws InterruptedException, IOException, ApiException {
        getAndSetDefaultClient();
        Attach attach = new Attach();
        final Attach.AttachResult result = attach.attach("default", "nginx-4217019353-k5sn9", true);

        new Thread(
                () -> {
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    OutputStream output = result.getStandardInputStream();
                    try {
                        while (true) {
                            String line = in.readLine();
                            output.write(line.getBytes());
                            output.write('\n');
                            output.flush();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                })
                .start();

        new Thread(
                () -> {
                    try {
                        Streams.copy(result.getStandardOutputStream(), System.out);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                })
                .start();

        Thread.sleep(10 * 1000);
        result.close();
        System.exit(0);
    }

    /**
     * Copy files and directories to and from containers, equal to kubectl cp
     */
    @Test
    public void copy() throws IOException, ApiException, CopyNotSupportedException {
        getAndSetDefaultClient();
        String podName = "kube-addon-manager-minikube";
        String namespace = "kube-system";


        Copy copy = new Copy();
        InputStream dataStream = copy.copyFileFromPod(namespace, podName, "/etc/motd");
        Streams.copy(dataStream, System.out);

        copy.copyDirectoryFromPod(namespace, podName, null, "/etc", Paths.get("/tmp/etc"));

        System.out.println("Done!");
    }

    /**
     * Establish an arbitrary web-socket session to certain resources.
     */
    static class WebSocketsExample {
        /**
         * This is a pretty low level, most people won't need to use WebSockets directly.
         *
         * <p>If you do need to run it, you can run: mvn exec:java \
         * -Dexec.mainClass=io.kubernetes.client.examples.WebSocketsExample \
         * -Dexec.args=/api/v1/namespaces/default/pods/<podname>/attach?stdout=true
         *
         * <p>Note that you'd think 'watch' calls were WebSockets, but you'd be wrong, they're straight HTTP
         * GET calls.
         */
        public static void main(String... args) throws ApiException, IOException {
            final ApiClient client = Config.defaultClient();
            WebSockets.stream(
                    args[0],
                    "GET",
                    client,
                    new WebSockets.SocketListener() {
                        private volatile WebSocket socket;

                        @Override
                        public void open(String protocol, WebSocket socket) {
                            this.socket = socket;
                        }

                        @Override
                        public void close() {
                        }

                        @Override
                        public void bytesMessage(InputStream is) {
                        }

                        @Override
                        public void failure(Throwable t) {
                            t.printStackTrace();
                        }

                        @Override
                        public void textMessage(Reader in) {
                            try {
                                BufferedReader reader = new BufferedReader(in);
                                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                                    System.out.println(line);
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        }
    }
    /*
        ################################   Advanced   #######################################
     */

    /**
     * Build an informer which list-watches resources and reflects the notifications to a local cache
     */
    public void Informer() throws IOException, ApiException, InterruptedException {
        getAndSetDefaultClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        ApiClient apiClient = coreV1Api.getApiClient();
        OkHttpClient httpClient =
                apiClient.getHttpClient().newBuilder().readTimeout(0, TimeUnit.SECONDS).build();
        apiClient.setHttpClient(httpClient);

        SharedInformerFactory factory = new SharedInformerFactory(apiClient);

        // Node informer
        SharedIndexInformer<V1Node> nodeInformer =
                factory.sharedIndexInformerFor(
                        (CallGeneratorParams params) -> {
                            // **NOTE**:
                            // The following "CallGeneratorParams" lambda merely generates a stateless
                            // HTTPs requests, the effective apiClient is the one specified when constructing
                            // the informer-factory.
                            return coreV1Api.listNodeCall(
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    params.resourceVersion,
                                    null,
                                    params.timeoutSeconds,
                                    params.watch,
                                    null);
                        },
                        V1Node.class,
                        V1NodeList.class);

        nodeInformer.addEventHandler(
                new ResourceEventHandler<V1Node>() {
                    @Override
                    public void onAdd(V1Node node) {
                        System.out.printf("%s node added!\n", Objects.requireNonNull(node.getMetadata()).getName());
                    }

                    @Override
                    public void onUpdate(V1Node oldNode, V1Node newNode) {
                        System.out.printf(
                                "%s => %s node updated!\n",
                                Objects.requireNonNull(oldNode.getMetadata()).getName(), Objects.requireNonNull(newNode.getMetadata()).getName());
                    }

                    @Override
                    public void onDelete(V1Node node, boolean deletedFinalStateUnknown) {
                        System.out.printf("%s node deleted!\n", Objects.requireNonNull(node.getMetadata()).getName());
                    }
                });

        factory.startAllRegisteredInformers();

        V1Node nodeToCreate = new V1Node();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName("noxu");
        nodeToCreate.setMetadata(metadata);
        V1Node createdNode = coreV1Api.createNode(nodeToCreate, null, null, null);
        Thread.sleep(3000);

        Lister<V1Node> nodeLister = new Lister<>(nodeInformer.getIndexer());
        V1Node node = nodeLister.get("noxu");
        System.out.printf("noxu created! %s\n", Objects.requireNonNull(node.getMetadata()).getCreationTimestamp());
        factory.stopAllRegisteredInformers();
        Thread.sleep(3000);
        System.out.println("informer stopped..");
    }

    /**
     * Support Pagination (only for the list request) to ease server-side loads/network congestion.
     */
    public void pager() throws IOException {
        ApiClient client = getAndSetDefaultClient();
        OkHttpClient httpClient =
                client.getHttpClient().newBuilder().readTimeout(60, TimeUnit.SECONDS).build();
        client.setHttpClient(httpClient);
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();
        int i = 0;
        Pager<V1Namespace, V1NamespaceList> pager =
                new Pager<>(
                        (Pager.PagerParams param) -> {
                            try {
                                return api.listNamespaceCall(
                                        null,
                                        null,
                                        param.getContinueToken(),
                                        null,
                                        null,
                                        param.getLimit(),
                                        null,
                                        null,
                                        1,
                                        null,
                                        null);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        },
                        client,
                        10,
                        V1NamespaceList.class);
        for (V1Namespace namespace : pager) {
            System.out.println(Objects.requireNonNull(namespace.getMetadata()).getName());
        }
        System.out.println("------------------");
    }


    /**
     * Build a controller reconciling the state of world by list-watching one or multiple resources.
     */
    public void controller() throws IOException {
        getAndSetDefaultClient();
        CoreV1Api coreV1Api = new CoreV1Api();
        ApiClient apiClient = coreV1Api.getApiClient();
        OkHttpClient httpClient =
                apiClient.getHttpClient().newBuilder().readTimeout(0, TimeUnit.SECONDS).build();
        apiClient.setHttpClient(httpClient);

        // instantiating an informer-factory, and there should be only one informer-factory
        // globally.
        SharedInformerFactory informerFactory = new SharedInformerFactory();
        // registering node-informer into the informer-factory.
        SharedIndexInformer<V1Node> nodeInformer =
                informerFactory.sharedIndexInformerFor(
                        (CallGeneratorParams params) -> coreV1Api.listNodeCall(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                params.resourceVersion,
                                null,
                                params.timeoutSeconds,
                                params.watch,
                                null),
                        V1Node.class,
                        V1NodeList.class);
        informerFactory.startAllRegisteredInformers();

        EventBroadcaster eventBroadcaster = new LegacyEventBroadcaster(coreV1Api);

        // nodeReconciler prints node information on events
        NodePrintingReconciler nodeReconciler =
                new NodePrintingReconciler(
                        nodeInformer,
                        eventBroadcaster.newRecorder(
                                new V1EventSource().host("localhost").component("node-printer")));

        // Use builder library to construct a default controller.
        Controller controller =
                ControllerBuilder.defaultBuilder(informerFactory)
                        .watch(
                                (workQueue) ->
                                        ControllerBuilder.controllerWatchBuilder(V1Node.class, workQueue)
                                                .withWorkQueueKeyFunc(
                                                        (V1Node node) ->
                                                                new Request(node.getMetadata().getName())) // optional, default to
                                                .withOnAddFilter(
                                                        (V1Node createdNode) ->
                                                                createdNode
                                                                        .getMetadata()
                                                                        .getName()
                                                                        .startsWith("docker-")) // optional, set onAdd filter
                                                .withOnUpdateFilter(
                                                        (V1Node oldNode, V1Node newNode) ->
                                                                newNode
                                                                        .getMetadata()
                                                                        .getName()
                                                                        .startsWith("docker-")) // optional, set onUpdate filter
                                                .withOnDeleteFilter(
                                                        (V1Node deletedNode, Boolean stateUnknown) ->
                                                                deletedNode
                                                                        .getMetadata()
                                                                        .getName()
                                                                        .startsWith("docker-")) // optional, set onDelete filter
                                                .build())
                        .withReconciler(nodeReconciler) // required, set the actual reconciler
                        .withName("node-printing-controller") // optional, set name for controller
                        .withWorkerCount(4) // optional, set worker thread count
                        .withReadyFunc(nodeInformer::hasSynced) // optional, only starts controller when the
                        // cache has synced up
                        .build();

        // Use builder library to manage one or multiple controllers.
        ControllerManager controllerManager =
                ControllerBuilder.controllerManagerBuilder(informerFactory)
                        .addController(controller)
                        .build();

        LeaderElectingController leaderElectingController =
                new LeaderElectingController(
                        new LeaderElector(
                                new LeaderElectionConfig(
                                        new EndpointsLock("kube-system", "leader-election", "foo"),
                                        Duration.ofMillis(10000),
                                        Duration.ofMillis(8000),
                                        Duration.ofMillis(5000))),
                        controllerManager);

        leaderElectingController.run();
    }

    static class NodePrintingReconciler implements Reconciler {

        private final Lister<V1Node> nodeLister;
        private final EventRecorder eventRecorder;

        public NodePrintingReconciler(
                SharedIndexInformer<V1Node> nodeInformer, EventRecorder recorder) {
            this.nodeLister = new Lister<>(nodeInformer.getIndexer());
            this.eventRecorder = recorder;
        }

        @Override
        public Result reconcile(Request request) {
            V1Node node = this.nodeLister.get(request.getName());
            System.out.println("triggered reconciling " + node.getMetadata().getName());
            this.eventRecorder.event(
                    node,
                    EventType.Normal,
                    "Print Node",
                    "Successfully printed %s",
                    node.getMetadata().getName());
            return new Result(false);
        }
    }

    /**
     * Leader election utilities to help implement HA controllers.
     */
    public void leaderElection() throws IOException {
        getAndSetDefaultClient();

        // New
        String appNamespace = "default";
        String appName = "leader-election-foobar";
        String lockHolderIdentityName = UUID.randomUUID().toString(); // Anything unique
        EndpointsLock lock = new EndpointsLock(appNamespace, appName, lockHolderIdentityName);

        LeaderElectionConfig leaderElectionConfig =
                new LeaderElectionConfig(
                        lock, Duration.ofMillis(10000), Duration.ofMillis(8000), Duration.ofMillis(2000));
        try (LeaderElector leaderElector = new LeaderElector(leaderElectionConfig)) {
            leaderElector.run(
                    () -> {
                        System.out.println("Do something when getting leadership.");
                    },
                    () -> {
                        System.out.println("Do something when losing leadership.");
                    });
        }
    }

    /**
     * Construct a generic client interface for any kubernetes types, including CRDs.
     */
    public void genericClient() throws IOException, ApiException {
        // The following codes demonstrates using generic client to manipulate pods
        V1Pod pod =
                new V1Pod()
                        .metadata(new V1ObjectMeta().name("foo").namespace("default"))
                        .spec(
                                new V1PodSpec()
                                        .containers(Arrays.asList(new V1Container().name("c").image("test"))));

        ApiClient apiClient = ClientBuilder.standard().build();
        GenericKubernetesApi<V1Pod, V1PodList> podClient =
                new GenericKubernetesApi<>(V1Pod.class, V1PodList.class, "", "v1", "pods", apiClient);

        V1Pod latestPod = podClient.create(pod).throwsApiException().getObject();
        System.out.println("Created!");

        V1Pod patchedPod =
                podClient
                        .patch(
                                "default",
                                "foo",
                                V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH,
                                new V1Patch("{\"metadata\":{\"finalizers\":[\"example.io/foo\"]}}"))
                        .throwsApiException()
                        .getObject();
        System.out.println("Patched!");

        V1Pod deletedPod = podClient.delete("default", "foo").throwsApiException().getObject();
        if (deletedPod != null) {
            System.out.println(
                    "Received after-deletion status of the requested object, will be deleting in background!");
        }
        System.out.println("Deleted!");
    }

    /**
     * Manipulating either native Kubernetes types or custom resources without generating classes.
     */
    public void dynamicClient() throws IOException, ApiException {
        ApiClient apiClient = ClientBuilder.standard().build();

        // retrieving the latest state of the default namespace
        DynamicKubernetesApi dynamicApi = new DynamicKubernetesApi("", "v1", "namespaces", apiClient);
        DynamicKubernetesObject defaultNamespace =
                dynamicApi.get("default").throwsApiException().getObject();

        // attaching a "foo=bar" label to the default namespace
        defaultNamespace.setMetadata(defaultNamespace.getMetadata().putLabelsItem("foo", "bar"));
        DynamicKubernetesObject updatedDefaultNamespace =
                dynamicApi.update(defaultNamespace).throwsApiException().getObject();

        System.out.println(updatedDefaultNamespace);

        apiClient.getHttpClient().connectionPool().evictAll();
    }
}