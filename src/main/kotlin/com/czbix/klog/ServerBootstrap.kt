package com.czbix.klog

import com.czbix.klog.http.core.HttpServerHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpContentCompressor
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import org.apache.logging.log4j.LogManager
import java.io.Closeable
import io.netty.bootstrap.ServerBootstrap as NettyBootstrap

class ServerBootstrap : Closeable {
    companion object {
        private val logger = LogManager.getLogger(ServerBootstrap::class.java)
    }

    private val port = 8000

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private lateinit var bootstrap: NettyBootstrap

    init {
        bootstrap = NettyBootstrap().apply {
            localAddress(port)
            group(bossGroup, workerGroup)
            channel(NioServerSocketChannel::class.java)
            option(ChannelOption.TCP_NODELAY, true)
            childOption(ChannelOption.SO_KEEPALIVE, true)
            childHandler(ServerInitializer())
        }
    }

    fun run() {
        use {
            val f = bootstrap.bind().sync()

            if (f.isSuccess) {
                logger.info("Server listening on {}", port)
            }

            f.channel().closeFuture().sync()
        }
    }

    override fun close() {
        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }

    class ServerInitializer : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
            ch.pipeline().apply {
                addLast("http", HttpServerCodec())
                addLast("compressor", HttpContentCompressor())
                addLast("aggregator", HttpObjectAggregator(1024 * 1024))
                addLast("http_server", HttpServerHandler())
            }
        }
    }
}
