package services

import context.TestContext
import models.TodoModel
import org.apache.http.HttpStatus
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

class PerformanceMeterService(config: Map<String, String>) {
    private val threadCount = config["threadCount"]!!.toInt()
    private val requestsCount = config["requestsCount"]!!.toInt()

    fun exexecutePerformanceMeter(f: (input: TodoModel) -> HttpUriRequest) {
        executePerformanceMeter(TestContext.createRandomTodos(requestsCount).map { f.invoke(it) })
    }

    fun executePerformanceMeter(requests: List<HttpUriRequest>) {
        val connManager = PoolingHttpClientConnectionManager()
        val portionSize = requestsCount / threadCount
        val executor = Executors.newFixedThreadPool(threadCount)
        val start = System.currentTimeMillis()
        for (i in 0 until threadCount) {
            executor.execute(
                HttpRequesterThread(
                    requests.subList(portionSize * i, portionSize * (i + 1)),
                    connManager
                )
            )
        }
        val end = System.currentTimeMillis();
        executor.shutdown()
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (e: Exception) {
            println(e.message)
        }
        println("RPS: ${requestsCount / ((end - start) / 1000.0)}")
        ReportCollector.showReport()
    }
}

class HttpRequesterThread(
    private val requests: List<HttpUriRequest>,
    private val connectionManager: PoolingHttpClientConnectionManager
) : Thread() {
    private val client = HttpClients.custom().setConnectionManager(connectionManager).build()
    override fun run() {
        requests.forEach {
            var response: CloseableHttpResponse
            val millis = measureTimeMillis {
                response = client.execute(it)
            }
            ReportCollector.addRow(it, ReportRow(millis, response.statusLine.statusCode))
        }
    }
}

data class ReportRow(val latency: Long, val statusCode: Int)

object ReportCollector {
    private val report: ConcurrentHashMap<HttpUriRequest, ReportRow> = ConcurrentHashMap()
    fun addRow(id: HttpUriRequest, reportRow: ReportRow) {
        report[id] = reportRow
    }

    fun calculatePrecentil(perccentil: Int, count: Int) = (perccentil * (count) / 100) - 1

    fun showReport() {
        val latencies = report.entries.map { it.value.latency }.sortedBy { it }
        val numRequests = report.entries.size
        val failed = report.entries.filter { it.value.statusCode != HttpStatus.SC_CREATED }.size / numRequests
        val p50 = latencies[calculatePrecentil(50, numRequests)]
        val p90 = latencies[calculatePrecentil(90, numRequests)];
        val p99 = latencies[calculatePrecentil(99, numRequests)];
        val p100 = latencies[calculatePrecentil(100, numRequests)];
        println("Num requests: $numRequests Num failed request: $failed P50: $p50 ms P90:  $p90 ms P99: $p99 ms P100: $p100 ms")

    }
}