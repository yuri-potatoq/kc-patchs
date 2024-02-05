package com.github.yuri_potatoq.gen_combinator

import arrow.core.Option
import arrow.core.Some
import arrow.core.none


enum class JTokens {
    JString,
    JIdent,
}


data class ASTNodeProps(
    var parent: Option<ASTNode>,
    var next: Option<ASTNode>,
    var inner: Option<ASTNode>
) {
    companion object {
        fun newEmpty() = ASTNodeProps(none(), none(), none())
    }
}


interface NodeFormatter {

}

object NopNodeFormatter : NodeFormatter {

}


sealed class ASTNode(val props: ASTNodeProps) {
    val formatters: MutableList<NodeFormatter> = mutableListOf()

    fun addInner(node: ASTNode) {
        if (props.inner.isSome()) {
            props.inner.onSome {
                it.addNext(node)
            }
            return
        }
        node.props.parent = Some(this)
        props.inner = Some(node)
    }

    fun addNext(node: ASTNode) {
        if (props.next.isSome()) {
            props.next.onSome {
                it.addNext(node)
            }
            return
        }

        node.props.parent = Some(this)
        props.next = Some(node)
    }

    open fun formatter() = NopNodeFormatter
}


class JDocumentNode(props: ASTNodeProps): ASTNode(props) {
    override fun render(out: Appendable) {
        props.inner.onSome { it.render(out) }
    }
}

class JClassNode(val name: String, props: ASTNodeProps): ASTNode(props) {
    override fun render(out: Appendable) {
        out.append("class $name {")
        props.inner.onSome { it.render(out) }
        out.append("}")
    }

}

class JMethodNode(val name: String, val returnType: String, props: ASTNodeProps): ASTNode(props) {
    override fun render(out: Appendable) {
        out.append("$returnType $name { }")
    }
}

fun JDocumentNode.jClass(name: String, block : JClassNode.() -> Unit) =
    JClassNode(name, ASTNodeProps.newEmpty()).apply {
        this@jClass.addInner(this@apply) // add to JDocumentNode instance
        this@apply.block()
    }


fun JClassNode.jMethod(name: String, block : ASTNode.() -> Unit) =
    JMethodNode(name, "String", ASTNodeProps.newEmpty()).apply {
        this@jMethod.addInner(this@apply) // add to JClassNode instance
        this@apply.block()
    }


fun jDocument(block: JDocumentNode.() -> Unit): ASTNode =
    JDocumentNode(ASTNodeProps.newEmpty()).apply {
        this.block()
    }


fun genAST(out: Appendable, node: ASTNode) {

    node.props.inner.onSome { it.render(out) }
    if (node.props.next.isSome()) {
        genAST(out, node.props.next.getOrNull()!!)
    }
}


fun main() {

    val ast = jDocument {
        jClass("Main") {
            jMethod("main") {}
        }
    }
    genAST(System.out!!, ast)

    System.out.flush()
/*
    val root = newNode {
        this.lines.add("root")
        child(mutableListOf("test1")) {
            child(mutableListOf("test2")) { }
        }
    }

    println(root.lines)
    println(root.childNode?.lines)
    println(root.childNode?.childNode?.lines)
    */

}


class Node() {
    var childNode: Node? = null
    var parent: Node? = null
    val lines: MutableList<String> = mutableListOf()

    fun addAllLines(xs: List<String>) {
        lines.addAll(xs)
        parent?.addAllLines(xs)
    }
}

fun newNode(block: Node.() -> Unit) = Node().apply {
    this.block()
}

fun Node.child(lines: MutableList<String>, block: Node.() -> Unit) {
    val newNode = Node()
    newNode.parent = this
    newNode.addAllLines(lines)

    this.childNode = newNode
    newNode.block()
}