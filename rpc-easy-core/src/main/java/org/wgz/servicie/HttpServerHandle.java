package org.wgz.servicie;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.wgz.model.RpcRequest;
import org.wgz.model.RpcResponse;
import org.wgz.registry.LocalRegistry;
import org.wgz.serializer.JdkSerializer;
import org.wgz.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * HTTP 请求处理
 */
@Slf4j
public class HttpServerHandle implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        //指定序列化器
        final JdkSerializer jdkSerializer = new JdkSerializer();

        //记录日志
        System.out.println("Received request: " + httpServerRequest.method() + " " + httpServerRequest.uri());

        httpServerRequest.handler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;

            try {
                rpcRequest = jdkSerializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null) {
                rpcResponse.setMessage("RpcRequest is null");
                doResponse(httpServerRequest, rpcResponse, jdkSerializer);
                return;
            }

            //获取请求参数
            String serviceName = rpcRequest.getServiceName();
            String methodName = rpcRequest.getMethodName();
            Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
            Object[] args = rpcRequest.getArgs();

            try {
                //获取服务实现类和服务的方法
                Class<?> implClass = LocalRegistry.getService(serviceName);
                Method method = implClass.getMethod(methodName, parameterTypes);
                Object result = method.invoke(implClass.getDeclaredConstructor().newInstance(), args);

                //封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("OK");

            } catch (Exception e) {
                log.info(e.getMessage());
                rpcResponse.setException(e);
                rpcResponse.setMessage(e.getMessage());
            }
            doResponse(httpServerRequest, rpcResponse, jdkSerializer);

        });

    }

    /**
     * 响应
     *
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    public void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse response = request.response().putHeader("content-type", "application/json");

        try {
            //序列化
            byte[] bytes = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            response.end(e.getMessage());
        }
    }
}
