require 'rest-client'

test_input = File.read(ENV['TEST_INPUT_FILE_LOCATION'])

puts "Test input: #{test_input}\n"

base_url = test_input.scan(/testing_endpoint:'(.+)'/)[0][0]

# "tests" here
responses = [
    RestClient.get("#{base_url}/api/inventory"),
    RestClient.get("#{base_url}/api/inventory/1"),
    RestClient.get("#{base_url}/api/validateMessage"),
    RestClient.get("#{base_url}/api/behaviors")
]

responses.each do |response|
    puts "Request: #{response.request.url}, Status: #{response.code}"
end

if responses.any? { |response| response.code != 200 }
    puts "A test failed"
    exit(1)
end
