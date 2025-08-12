#!/bin/bash

# Test script for JMX connectivity and metrics
echo "🔍 Testing Sakila JMX Metrics..."

# Check if application is running
echo "📡 Checking if application is accessible..."
if curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo "✅ Application is running on port 8080"
else
    echo "❌ Application is not accessible on port 8080"
    echo "   Please run: docker-compose up -d"
    exit 1
fi

# Test JMX HTTP endpoint
echo "🌐 Testing JMX via HTTP endpoint..."
if curl -s "http://localhost:8080/actuator/jmx" > /dev/null; then
    echo "✅ JMX HTTP endpoint is accessible"
else
    echo "❌ JMX HTTP endpoint is not accessible"
fi

# Test specific MBeans via HTTP
echo "📊 Testing Movie Search Metrics MBean..."
SEARCH_METRICS=$(curl -s "http://localhost:8080/actuator/jmx/sakila%3Atype%3DMovieSearchMetrics%2Cname%3DSearchCounter" 2>/dev/null)
if [ $? -eq 0 ] && [ -n "$SEARCH_METRICS" ]; then
    echo "✅ Movie Search Metrics MBean is accessible"
    echo "   Current metrics: $(echo $SEARCH_METRICS | jq -r '.attributes.TotalMoviesReturned.value // "N/A"') movies returned from $(echo $SEARCH_METRICS | jq -r '.attributes.TotalSearchOperations.value // "N/A"') searches"
else
    echo "❌ Movie Search Metrics MBean is not accessible"
fi

echo "🔌 Testing Database Connection Metrics MBean..."
DB_METRICS=$(curl -s "http://localhost:8080/actuator/jmx/sakila%3Atype%3DDatabaseConnectionMetrics%2Cname%3DConnectionPool" 2>/dev/null)
if [ $? -eq 0 ] && [ -n "$DB_METRICS" ]; then
    echo "✅ Database Connection Metrics MBean is accessible"
    echo "   Current connections: $(echo $DB_METRICS | jq -r '.attributes.CurrentActiveConnections.value // "N/A"') active, $(echo $DB_METRICS | jq -r '.attributes.IdleConnections.value // "N/A"') idle"
else
    echo "❌ Database Connection Metrics MBean is not accessible"
fi

# Test JMX port connectivity
echo "🔧 Testing JMX port 1099..."
if timeout 5 bash -c "</dev/tcp/localhost/1099" 2>/dev/null; then
    echo "✅ JMX port 1099 is accessible"
    echo "   You can now connect with JConsole using: localhost:1099"
else
    echo "❌ JMX port 1099 is not accessible"
    echo "   Make sure Docker container is running and port is mapped"
fi

# Generate some test data
echo "🎬 Generating test search data..."
echo "   Making search requests to increment metrics..."
curl -s "http://localhost:8080/api/films/search?title=action" > /dev/null
curl -s "http://localhost:8080/api/films/search?title=comedy" > /dev/null
curl -s "http://localhost:8080/api/films/search?title=drama" > /dev/null

echo "✅ Test data generated. Check JMX metrics to see updated counters!"

echo ""
echo "🎯 Next Steps:"
echo "   1. Open JConsole"
echo "   2. Connect to: localhost:1099"
echo "   3. Navigate to MBeans > sakila"
echo "   4. Explore MovieSearchMetrics and DatabaseConnectionMetrics"
echo ""
echo "📖 For detailed instructions, see: JMX_METRICS_README.md"
