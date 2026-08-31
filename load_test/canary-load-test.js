import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 10,
    duration: '30s',

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: [
            'p(95)<500',
            'p(99)<1000',
        ],
    },
};

export default function () {

    const url = 'http://127.0.0.1:8080/api/v1/orders';

    const payload = JSON.stringify({
        items: [
            {
                productName: 'Laptop',
                unitPrice: 1000,
                quantity: 1,
            },
        ],
    });

    const params = {
        headers: {
            Host: 'ddd.local',
            'Content-Type': 'application/json',

            // Canary traffic
            'X-Canary': 'true',

            // Unique idempotency key
            'Idempotency-Key': `canary-${__VU}-${__ITER}`,
        },
    };

    const response = http.post(url, payload, params);

    check(response, {
        'status is 201': (r) => r.status === 201,
    });
}