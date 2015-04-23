package server

import "fmt"

type MsgType int

const (
	MsgLogin MsgType = iota //
	MsgQuery                //
	MsgReply
	MsgExit //
)

// Message represents a message used by the LSP protocol.
type Message struct {
	Type   MsgType // One of the message types listed above.
	Mid    string  // mobile device
	Ipaddr string  // ip addr.
	Port   int     // port.
	Peers  string  // peer list.
}

// NewData returns a new data message with the specified connection ID,
// sequence number, and payload.
func NewReply(ip string, pt int, peers string) *Message {
	return &Message{
		Type:   MsgReply,
		Ipaddr: ip,
		Port:   pt,
		Peers:  peers,
	}
}

// String returns a string representation of this message. To pretty-print a
// message, you can pass it to a format string like so:
//     msg := NewConnect()
//     fmt.Printf("Connect message: %s\n", msg)
func (m *Message) String() string {
	var name, payload string
	switch m.Type {
	case MsgLogin:
		name = "Login type"
		payload = m.Mid
	case MsgQuery:
		name = "query type"
		payload = " " + m.Peers
	case MsgReply:
		name = "reply type"
		payload = "" + m.Peers
	case MsgExit:
		name = "Exit type"
		payload = ""
	}
	return fmt.Sprintf("[%s %s %d %s]", name, m.Ipaddr, m.Port, payload)
}
