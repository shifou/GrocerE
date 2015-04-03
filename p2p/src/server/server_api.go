package server

type Server interface {

	// Start starts the server on a distinct port and begins listening
	// for incoming client connections. Start returns an error if there was
	// a problem listening on the specified port.
	//
	// This method should NOT block. Instead, it should spawn one or more
	// goroutines (to handle things like accepting incoming client connections,
	// broadcasting messages to clients, synchronizing access to the server's
	// set of connected clients, etc.) and then return.
	//
	// This method should return an error if the server has already been closed.
	Start(port int) error

	// Close shuts down the server. All client connections should be closed immediately
	// and any goroutines running in the background should be signaled to return.
	Close()
}
