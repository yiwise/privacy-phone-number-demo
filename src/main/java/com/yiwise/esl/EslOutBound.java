package com.yiwise.esl;

import com.alibaba.fastjson.JSON;
import com.yiwise.service.PhoneConvertService;
import org.freeswitch.esl.client.outbound.AbstractOutboundClientHandler;
import org.freeswitch.esl.client.outbound.AbstractOutboundPipelineFactory;
import org.freeswitch.esl.client.outbound.SocketClient;
import org.freeswitch.esl.client.transport.SendMsg;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.freeswitch.esl.client.transport.message.EslHeaders;
import org.freeswitch.esl.client.transport.message.EslMessage;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EslOutBound {
    public static Logger logger = LoggerFactory.getLogger(EslOutBound.class);

    SocketClient outboundServer = null;

    @Resource(name = "simplePhoneConvertService")
    PhoneConvertService phoneConvertService;

    public void createOutboundServer() {
        int eslPort = PropertyLoader.getIntProperty("fs.esl.port");
        outboundServer = new SocketClient(eslPort,
                new AbstractOutboundPipelineFactory() {
                    @Override
                    protected AbstractOutboundClientHandler makeHandler() {
                        return new AbstractOutboundClientHandler() {
                            ThreadLocal<MessageEvent> currMsgLocal = new ThreadLocal<>();

                            @Override
                            public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
                                currMsgLocal.set(e);
                                super.messageReceived(ctx, e);
                            }

                            @Override
                            protected void handleConnectResponse(ChannelHandlerContext ctx, EslEvent event) {
                                logger.info("Received connect response [{}]", event);
                                logger.info("remote address=" + ctx.getChannel().getRemoteAddress());
                                if (!event.getEventName().equalsIgnoreCase("CHANNEL_DATA")) {
                                    throw new IllegalStateException("Unexpected event after connect: [" + event.getEventName() + ']');
                                }
                                // this is the response to the initial connect
                                logger.info("=======================  incoming channel data  =============================");
                                logger.info("Event-Date-Local: [{}]", event.getEventDateLocal());
                                logger.info("Unique-ID: [{}]", event.getEventHeaders().get("Unique-ID"));
                                logger.info("Channel-ANI: [{}]", event.getEventHeaders().get("Channel-ANI"));
                                logger.info("Answer-State: [{}]", event.getEventHeaders().get("Answer-State"));
                                logger.info("Caller-Destination-Number: [{}]", event.getEventHeaders().get("Caller-Destination-Number"));
                                logger.info("FreeSWITCH-Hostname: [{}]", event.getEventHeaders().get("FreeSWITCH-Hostname"));
                                logger.info("FreeSWITCH-IPv4: [{}]", event.getEventHeaders().get("FreeSWITCH-IPv4"));
                                logger.info("=======================  = = = = = = = = = = =  =============================");

                                // 获取加密的号码
                                String encriptedNumber = event.getEventHeaders().get("variable_encripted_number");
                                String originalNumber = phoneConvertService.decript(encriptedNumber);

                                try {
                                    eslExecute(ctx.getChannel(), "set", "original_number=" + originalNumber);
                                    String bridgeMode = PropertyLoader.getProperty("fs.bridge.mode");
                                    eslExecute(ctx.getChannel(), "bridge", bridgeMode);
                                } catch (Exception e) {
                                    logger.error("csEslOutBound error,", e);
                                }
                            }

                            @Override
                            protected void handleEslEvent(ChannelHandlerContext ctx, EslEvent event) {
                                logger.info("outbound receive event");
                            }

                            @Override
                            public void handleDisconnectionNotice() {
                                MessageEvent e = currMsgLocal.get();
                                EslMessage eslMsg = (EslMessage) e.getMessage();
                                logger.info("Received disconnection notice :");
                                logger.info("------------------" + JSON.toJSONString(eslMsg.getHeaders()));
                                logger.info("------------------" + JSON.toJSONString(eslMsg.getBodyLines()));
                            }

                            public boolean eslExecute(Channel channel, String app, String args) {
                                if (!channel.isConnected()) {
                                    if (logger.isWarnEnabled()) {
                                        logger.warn("channel is not connected any more: " + args);
                                    }
                                    return false;
                                }
                                SendMsg msg = new SendMsg();
                                msg.addCallCommand("execute");
                                msg.addExecuteAppName(app);
                                msg.addExecuteAppArg(args);
                                EslMessage response = sendSyncMultiLineCommand(channel, msg.getMsgLines());
                                if (response.getHeaderValue(EslHeaders.Name.REPLY_TEXT).startsWith("+OK")) {
                                    log.info("bridge call successful");
                                } else {
                                    log.error("bridge call failed: [{}}", response.getHeaderValue(EslHeaders.Name.REPLY_TEXT));
                                }
                                return true;
                            }
                        };
                    }
                }
        );
        outboundServer.start();
    }

    public void destoryOutboundServer() {
        outboundServer.stop();
    }
}
