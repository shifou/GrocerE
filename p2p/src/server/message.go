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
	Type   MsgType  // One of the message types listed above.
	Ipaddr string   // Unique client-server connection ID.
	Port   int      // Message sequence number.
	Peers  []string // Data message payload.
}

// NewData returns a new data message with the specified connection ID,
// sequence number, and payload.
func NewReply(ip string, pt int, peers []string) *Message {
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
	var name
	payload string[]
	switch m.Type {
	case MsgLogin:
		name = "Login"
	case MsgQuery:
		name = "query"
		payload = " " + string(m.Peers)
	case MsgReply:
		name = "reply"
		payload = " " + string(m.Peers)
	case MsgExit:
		name = "Exit"
	}
	return fmt.Sprintf("[%s %s %d%s]", name, m.ipaddr, m.port, payload)
}
