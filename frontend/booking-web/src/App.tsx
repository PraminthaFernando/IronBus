import { useQuery } from '@tanstack/react-query';
import { getSystemStatus } from './api/systemApi';

function App() {
  const statusQuery = useQuery({
    queryKey: ['system-status'],
    queryFn: getSystemStatus,
  });

  if (statusQuery.isPending) {
    return <main>Connecting to booking service...</main>;
  }

  if (statusQuery.isError) {
    return (
      <main>
        <h1>Train Seat Booking</h1>
        <p>Backend connection failed.</p>
      </main>
    );
  }

  return (
    <main>
      <h1>Segment-Based Train Seat Booking</h1>
      <p>
        Backend status: {statusQuery.data.status}
      </p>
    </main>
  );
}

export default App;