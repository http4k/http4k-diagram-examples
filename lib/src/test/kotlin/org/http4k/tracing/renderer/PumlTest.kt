package org.http4k.tracing.renderer

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.testing.ApprovalTest
import org.http4k.testing.Approver
import org.http4k.testing.assertApproved
import org.http4k.tracing.entire_trace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ApprovalTest::class)
class PumlTest {

    @Test
    fun `sequence renders as expected`(approver: Approver) {
        val render = PumlSequenceDiagram.render("foobar", listOf(entire_trace))
        assertThat(render.title, equalTo("foobar - Sequence"))
        assertThat(render.format, equalTo("PUML"))
        approver.assertApproved(render.content)
    }

    @Test
    fun `interaction renders as expected`(approver: Approver) {
        val render = PumlInteractionDiagram.render("foobar", listOf(entire_trace))
        assertThat(render.title, equalTo("foobar - Interactions"))
        assertThat(render.format, equalTo("PUML"))
        approver.assertApproved(render.content)
    }

    @Test
    fun `interaction flow renders as expected`(approver: Approver) {
        val render = PumlInteractionFlowDiagram.render("foobar", listOf(entire_trace))
        assertThat(render.title, equalTo("foobar - Flow"))
        assertThat(render.format, equalTo("PUML"))
        approver.assertApproved(render.content)
    }

}