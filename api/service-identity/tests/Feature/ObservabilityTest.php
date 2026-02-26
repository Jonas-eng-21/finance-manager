<?php

namespace Tests\Feature;

use Tests\TestCase;

class ObservabilityTest extends TestCase
{
    public function test_it_returns_correlation_id_in_headers(): void
    {
        $response = $this->postJson('/api/login');

        $response->assertHeader('X-Correlation-ID');

        $this->assertNotEmpty($response->headers->get('X-Correlation-ID'));
    }
}
