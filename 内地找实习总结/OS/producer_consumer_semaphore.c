int buffer[max];
int fill_ptr, use_ptr;
int count;

void put(int v) {
	buffer[fill_ptr] = v;
	fill_ptr = (fill_ptr + 1) % max;
	++count;
}

int get(){
	int res = buffer[use_ptr];
	use_ptr = (use_ptr - 1) % max;
	--count;	
}

sem_t mutex;
sem_t empty, full;
/**
 * empty: number of empty slots in the buffer. Initialized to MAX.
 * full: number of taken slot in the buffer. Initialized to full.
 * For producer: wait for empty to be non-negative, increment full when finished.
 * For consumer: wait for full to be non-negative, increment empty when finished.
 */

void *producer(void *arg) {
	for(int i = 0; i < loops; ++i) {
		sem_wait(&empty);
		sem_wait(&mutex);
		put(i);
		sem_post(&mutex);
		sem_post(&full);
	}
}

void *consumer(void *arg) {
	while(1) {
		sem_wait(&full);
		sem_wait(&mutex);
		int res = get();
		sem_post(&mutex);
		sem_post(&empty);
		return res;
	}
}

int main() {
	// ...
	sem_init(&full, 0, 0);
	sem_init(&empty, 0, MAX);
	sem_init(&mutex, 0, 1);
	// ...
}

