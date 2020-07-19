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

cond_t empty, fill;
mutex_t mutex;

cond_t_init(&empty);
cond_t_init(&fill);
mutex_t_init(&mutex);


void *producer(void *arg) {
	for(int i = 0; i < loops; ++i) {
		mutex_lock(&mutex);
		while(count == MAX) {
			cond_wait(&empty, &lock);
		}
		put(i);
		cond_signal(&fill);
		mutex_unlock(&mutex);
	}
}

void *consumer(void *arg) {
	while(1) {
		mutex_lock(&mutex);
		while(count == 0) {
			cond_wait(&fill, &lock);
		}
		int temp = get();
		cond_signal(&empty);
		mutex_unlock(&mutex);
		printf("%d\n", temp)
	}
}

